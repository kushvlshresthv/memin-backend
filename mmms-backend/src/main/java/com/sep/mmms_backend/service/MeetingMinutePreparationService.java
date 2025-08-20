package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import np.com.bahadur.converter.date.nepali.DateConverter;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MeetingMinutePreparationService {
    private final MeetingService meetingService;
    private final CommitteeRepository committeeRepository;
    private final MemberService memberService;
    private final TemplateEngine templateEngine;

    public MeetingMinutePreparationService(MeetingService meetingService, CommitteeRepository committeeRepository, MemberService memberService, TemplateEngine templateEngine) {
        this.meetingService = meetingService;
        this.memberService = memberService;
        this.committeeRepository = committeeRepository;
        this.templateEngine = templateEngine;
    }

    /**
     * Prepares all necessary data for the meeting minute view.
     *
     * @param committeeId The ID of the committee.
     * @param meetingId The ID of the meeting.
     * @param username The username of the authenticated  user .
     * @return A map containing all data attributes for the Thymeleaf model.
     * @throws CommitteeDoesNotExistException If the committee is not found in the database.
     * @throws MeetingDoesNotExistException If the mmeting is not found in the database.
     * @throws IllegalOperationException If the user does not have access to the committee/meeting.
     *
     */
    public Map<String, Object> prepareMeetingMinuteData(int committeeId, int meetingId, String username) {

        //1. validate committee is accessible by the user
        Committee committee = committeeRepository.findByIdNoException(committeeId).orElseThrow(CommitteeDoesNotExistException::new);

        if (!committee.getCreatedBy().getUsername().equals(username)) {
            throw new IllegalOperationException();
        }

        // 2. Validate Meeting and its association with Committee
        Meeting meeting = meetingService.findMeetingByIdNoException(meetingId)
                .orElseThrow(MeetingDoesNotExistException::new);

        if (!committee.getMeetings().contains(meeting)) {
            throw new IllegalOperationException();
        }

        // 3. Populate Data Map
        Map<String, Object> modelData = new HashMap<>();
        LocalDate meetingHeldDate = meeting.getHeldDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDateForBSConversion = meeting.getHeldDate().format(formatter);

        DateConverter dc = new DateConverter();
        try {
            modelData.put("meetingHeldDate", toNepaliDigits(dc.convertAdToBs(formattedDateForBSConversion).replace("-", "/")));
//            modelData.put("meetingHeldDate", toNepaliDigits(formattedDateForBSConversion));
        } catch(Exception e) {

            //TODO: Handle this exception
            System.out.println("TODO: Handle Exception");
        }

        formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String formattedDate = meeting.getHeldDate().format(formatter);
        modelData.put("meetingHeldDateEnglish", formattedDate);

        modelData.put("meetingHeldDay", getMeetingHeldDay(meeting.getHeldDate()));
        modelData.put("partOfDay", getPartOfDay(meeting.getHeldTime()));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");
        String meetingHeldTime = meeting.getHeldTime().format(timeFormatter);
        modelData.put("meetingHeldTime", toNepaliDigits(meetingHeldTime));
        modelData.put("meetingHeldPlace", meeting.getHeldPlace());
        modelData.put("committeeDescription", committee.getDescription());
        modelData.put("committeeName", meeting.getCommittee().getName());
        modelData.put("coordinatorFullName", getCoordinatorFullName(committee.getMemberships()));
        modelData.put("decisions", meeting.getDecisions());
        modelData.put("agendas", meeting.getAgendas());


        //This should be done last because this also parses the roles to nepali
        modelData.put("membershipsForMinute", getSortedMembershipsForMinute(committee, meeting, committeeId));

        return modelData;
    }


    private String toNepaliDigits(String input) {
        char[] nepaliDigits = {'०','१','२','३','४','५','६','७','८','९'};
        StringBuilder result = new StringBuilder();

        for (char ch : input.toCharArray()) {
            if (Character.isDigit(ch)) {
                result.append(nepaliDigits[ch - '0']);
            } else {
                result.append(ch); // keep dashes, colons, etc.
            }
        }
        return result.toString();
    }

    private String getMeetingHeldDay(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SUNDAY -> "आइतबार";
            case MONDAY -> "सोमबार";
            case TUESDAY -> "मंगलबार";
            case WEDNESDAY -> "बुधबार";
            case THURSDAY -> "बिहीबार";
            case FRIDAY -> "शुक्रबार";
            case SATURDAY -> "शनिबार";
            default -> "";
        };
    }

    private String getPartOfDay(LocalTime time) {
        int hour = time.getHour();
        String partOfDay;

        if (hour >= 5 && hour < 12) {
            partOfDay = "बिहान";
        } else if (hour >= 12 && hour < 17) {
            partOfDay = "दिउँसो";
        } else if (hour >= 17 && hour < 21) {
            partOfDay ="साँझ";
        } else {
            partOfDay = "राति";
        }
        return partOfDay;
    }

    private List<CommitteeMembership> getSortedMembershipsForMinute(Committee committee , Meeting meeting, int committeeId) {
        List<CommitteeMembership> membershipsForMinute = committee.getMemberships();
        for(Member invitee: meeting.getInvitees()) {
            CommitteeMembership inviteeMembership = new CommitteeMembership();
            inviteeMembership.setMember(invitee);
            inviteeMembership.setRole("Invitee");
            membershipsForMinute.add(inviteeMembership);
        }
        sortMembershipByRole(membershipsForMinute);
        parseRoleForMinute(committee.getMemberships());
        return membershipsForMinute;
    }

    /**
     * Sorts memberships object based on role in the order: 'coordinator -> member -> member_secretary -> invitee'
     */
    private void sortMembershipByRole(List<CommitteeMembership> memberships) {
        if (memberships == null || memberships.isEmpty()) {
            return;
        }

        Map<String, Integer> rolePriority = new HashMap<>();

        // 1. Top Leadership
        rolePriority.put("Coordinator", 1);
        rolePriority.put("Chairperson", 2);
        rolePriority.put("Vice Chairperson", 3);

        // 2. Executive / Key Decision-Makers
        rolePriority.put("Vice Coordinator", 4);
        rolePriority.put("Secretary", 5);
        rolePriority.put("Joint Secretary", 6);
        rolePriority.put("Member-Secretary", 7);

        // 3. Financial Oversight
        rolePriority.put("Treasurer", 8);

        // 4. Specialized Leadership Roles
        rolePriority.put("Program Coordinator", 9);
        rolePriority.put("Technical Coordinator", 10);
        rolePriority.put("Event Manager", 11);

        // 5. Communication & External Relations
        rolePriority.put("Spokesperson", 12);

        // 6. Advisory / Oversight
        rolePriority.put("Advisor", 13);
        rolePriority.put("Observer", 14);

        // 7. General Membership
        rolePriority.put("Member", 15);

        // 8. Invitee (lowest priority)
        rolePriority.put("Invitee", 17);

        int unknownRolePriority = 16; // just before Invitee

        memberships.sort(Comparator.comparingInt(m ->
                rolePriority.getOrDefault(m.getRole(), unknownRolePriority)
        ));
    }


    private void parseRoleForMinute(List<CommitteeMembership> memberships) {
        // Nepali translations mapped to English roles
        Map<String, String> roleTranslations = Map.ofEntries(
                // 1. Top Leadership
                Map.entry("coordinator", "संयोजक"),
                Map.entry("chairperson", "अध्यक्ष"),
                Map.entry("vice chairperson", "उपाध्यक्ष"),

                // 2. Executive / Key Decision-Makers
                Map.entry("vice coordinator", "उप-समन्वयक"),
                Map.entry("secretary", "सचिव"),
                Map.entry("joint secretary", "सह सचिव"),
                Map.entry("member-secretary", "सदस्य–सचिव"),

                // 3. Financial Oversight
                Map.entry("treasurer", "कोषाध्यक्ष"),

                // 4. Specialized Leadership Roles
                Map.entry("program coordinator", "कार्यक्रम समन्वयक"),
                Map.entry("technical coordinator", "प्राविधिक समन्वयक"),
                Map.entry("event manager", "कार्यक्रम प्रबन्धक"),

                // 5. Communication & External Relations
                Map.entry("spokesperson", "प्रवक्ता"),

                // 6. Advisory / Oversight
                Map.entry("advisor", "सल्लाहकार"),
                Map.entry("observer", "पर्यवेक्षक"),

                // 7. General Membership
                Map.entry("member", "सदस्य"),
                Map.entry("invitee", "आमन्त्रित")
        );

        // Loop through memberships
        for (CommitteeMembership membership : memberships) {
            String englishRoleLower = membership.getRole().toLowerCase();
            String translatedRole = roleTranslations.get(englishRoleLower);
            if (translatedRole != null) {
                membership.setRole(translatedRole);
            }
        }
    }

    private String getCoordinatorFullName(List<CommitteeMembership> memberships) {
        //TODO: Optimize (there might be better ways to get coordinator membership
        for(CommitteeMembership membership : memberships) {
            if(membership.getRole().equalsIgnoreCase("Coordinator"))
                return membership.getMember().getPost() + " " + membership.getMember().getFirstNameNepali() + " " + membership.getMember().getLastNameNepali();
        }

        return "[Error: No Coordinator]";
    }







    //----------------------------------------------------------------------------------
    //This part is for msword creation
    //TODO: Consider moving the code below to a separate service like: MeetingMinuteWordPreparationService


    public String renderHtmlTemplate(String templateName, Map<String, Object> dataModel) {
        Context context = new Context();
        dataModel.forEach(context::setVariable);

        return templateEngine.process(templateName, context);
    }

    /*
    Possible classes that our templates can have:
    1. introduction -> signifies sections
    2. justify-text -> styling
    3. heading -> styling
    4. memberships -> signifies sections
    5. decisions -> signifies sections

    Structure of the template:

    #a4-box
        #introduction
            #introduction-body
        #memberships
            #heading
            #table
        #agendas  (only have this section if agenda isn't empty)
            #heading-agendas
            #agendas-list
        #decisions
            #heading-decisions
            #decisions-list
    */

    public byte[] createWordDocumentFromHtml(String htmlContent) throws Exception {
        System.out.println(htmlContent);
        try (XWPFDocument document = new XWPFDocument()) {
            Document html = Jsoup.parse(htmlContent);

            XWPFParagraph paragraph = null;
            XWPFRun run = null;
            Element a4_box = html.getElementById("a4-box");
            if(a4_box == null) {
                throw new Exception();
            }



            for(Element element: a4_box.children()) {
                if(element.className().contains("introduction")) {

                    //rest of the classes(which are used for styling)
                    List<String> stylings = Arrays.asList(element.className().split("\\s+"));

                    Elements children = element.children();
                    for(Element child: children) {
                        if(child.className().equals("introduction-body")) {
                            paragraph = document.createParagraph();
                            paragraph.setSpacingAfter(100);
                            run = paragraph.createRun();
                            run.setText(element.text());

                            if(stylings.contains("justify-text")) {
                                styleJustifyText(paragraph);
                            }
                        }
                    }
                }

                else if (element.className().contains("memberships")) {
                    Elements children = element.children();

                    //attendee has two children, a heading, and the table
                    for(Element child: children) {

                        if(child.className().contains("heading")) {
                            paragraph = document.createParagraph();
                            paragraph.setSpacingBefore(100);
                            paragraph.setSpacingAfter(200);
                            styleHeading(paragraph.createRun(), child);
                        }

                        if(child.nodeName().equals("table")) {
                            XWPFTable newTable = document.createTable();
                            final int PADDING_LEFT = 100;
                            final int PADDING_TOP = 100;
                            newTable.setCellMargins(PADDING_TOP, PADDING_LEFT, 0,0 );
                            newTable.setWidth(XWPFTable.DEFAULT_PERCENTAGE_WIDTH);

                            copyTable(newTable, child);
                        }
                    }
                }


                else if(element.className().contains("agendas")) {
                    Elements children = element.children();

                    //deicisions has two children, a heading, and a list
                    for(Element child: children) {
                        if(child.className().contains("heading")) {
                            paragraph = document.createParagraph();
                            paragraph.setSpacingBefore(200);
                            paragraph.setSpacingAfter(200);
                            run = paragraph.createRun();
                            styleHeading(run, child);
                        }

                        if(child.nodeName().equals("ol")) {
                            Elements agendas = child.children();
                            int count = 1;
                            final int DECISION_SPACING = 17;

                            // 1. Create numbering instance
                            XWPFNumbering numbering = document.createNumbering();

                            // 2. Define abstract numbering style (numbered list)
                            CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();
                            abstractNum.setAbstractNumId(BigInteger.ZERO);

                            CTLvl level = abstractNum.addNewLvl();
                            level.setIlvl(BigInteger.ZERO);
                            level.addNewNumFmt().setVal(STNumberFormat.HINDI_NUMBERS);
                            level.addNewLvlText().setVal("%1.");
                            level.addNewStart().setVal(BigInteger.ONE);

                            // 3. Add the abstract numbering to document
                            XWPFAbstractNum xwpfAbstractNum = new XWPFAbstractNum(abstractNum);
                            BigInteger abstractNumID = numbering.addAbstractNum(xwpfAbstractNum);

                            // 4. Create a numbering instance for the list
                            BigInteger numID = numbering.addNum(abstractNumID);

                            // 5. Now add decisions with automatic numbering
                            for (Element agenda : agendas) {
                                paragraph = document.createParagraph();
                                paragraph.setNumID(numID);  // ← This line activates numbering!

                                // Fix line wrapping for numbered list
                                paragraph.setIndentationLeft(720);      // Indent whole paragraph
                                paragraph.setIndentationHanging(360);   // Hanging indent for number alignment

                                run = paragraph.createRun();
                                run.setText(agenda.text().substring(3));
                            }
                        }
                    }
                }

                else if(element.className().contains("decisions")) {
                    Elements children = element.children();

                    //deicisions has two children, a heading, and a list
                    for(Element child: children) {
                        if(child.className().contains("heading")) {
                            paragraph = document.createParagraph();
                            paragraph.setSpacingBefore(200);
                            paragraph.setSpacingAfter(200);
                            run = paragraph.createRun();
                            styleHeading(run, child);
                        }

                        if(child.nodeName().equals("ol")) {
                            Elements decisions = child.children();
                            int count = 1;
                            final int DECISION_SPACING = 17;

                            // 1. Create numbering instance
                            XWPFNumbering numbering = document.createNumbering();

                            // 2. Define abstract numbering style (numbered list)
                            CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();
                            abstractNum.setAbstractNumId(BigInteger.ONE);

                            CTLvl level = abstractNum.addNewLvl();
                            level.setIlvl(BigInteger.ZERO);
                            level.addNewNumFmt().setVal(STNumberFormat.HINDI_NUMBERS);
                            level.addNewLvlText().setVal("%1.");
                            level.addNewStart().setVal(BigInteger.ONE);

                            // 3. Add the abstract numbering to document
                            XWPFAbstractNum xwpfAbstractNum = new XWPFAbstractNum(abstractNum);
                            BigInteger abstractNumID = numbering.addAbstractNum(xwpfAbstractNum);

                            // 4. Create a numbering instance for the list
                            BigInteger numID = numbering.addNum(abstractNumID);

                            // 5. Now add decisions with automatic numbering
                            for (Element decision : decisions) {
                                paragraph = document.createParagraph();
                                paragraph.setNumID(numID);  // ← This line activates numbering!

                                // Fix line wrapping for numbered list
                                paragraph.setIndentationLeft(720);      // Indent whole paragraph
                                paragraph.setIndentationHanging(360);   // Hanging indent for number alignment

                                run = paragraph.createRun();
                                run.setText(decision.text().substring(3));
                            }
                        }
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            byte[] bytes = out.toByteArray();
            return bytes;
        } catch(Exception e) {
            throw e;
        }
    }


    public void styleJustifyText(XWPFParagraph paragraph) {
        paragraph.setAlignment(ParagraphAlignment.BOTH);
    }

    public void styleHeading(XWPFRun run, Element element) {
        run.setText(element.text());
        run.setBold(true);
        run.setUnderline(UnderlinePatterns.SINGLE);
    }



    //copies html table to msword table
    public void copyTable(XWPFTable newTable, Element oldTable) {
        //getting all the rows
        Elements oldRows = oldTable.select("tr");

        //iterate through the rows
        for(int i = 0; i<oldRows.size(); i++) {
            Element oldRow = oldRows.get(i);

            //getting the individual cells
            Elements oldCells = oldRow.select("th, td");

            //create new row(skip first because Apache POI creates one by default)
            XWPFTableRow newTableRow = (i==0) ? newTable.getRow(0): newTable.createRow();


            //set the min-height of the table row
            final int ROW_HEIGHT = 600;
            newTableRow.setHeight(ROW_HEIGHT);
            newTableRow.setHeightRule(TableRowHeightRule.AT_LEAST);

            //get the data from each cell and populate the XWPFTableRow
            for(int j = 0; j<oldCells.size(); j++) {
                String oldCellText = oldCells.get(j).text();
                //remove the first cell in the first row which is pre-built by the framework
                if(i==0 && j==0) {
                    newTableRow.removeCell(0);
                }

                XWPFTableCell cell = null;
                //only create new cells, if jth cell does not exist
                if(newTableRow.getTableCells().size()< j+1) {
                    cell = newTableRow.createCell();
                } else {
                    cell = newTableRow.getCell(j);
                }

                //remove the pre-built paragraph
                cell.removeParagraph(0);
                XWPFParagraph para = cell.addParagraph();
                XWPFRun run = para.createRun();
                run.setText(oldCellText);

                if(j==0) {
                    newTableRow.getCell(j).setWidth("5%");
                } else if(j==1 || j==2) {
                    newTableRow.getCell(j).setWidth("30%");
                } else if(j==3) {
                    newTableRow.getCell(j).setWidth("35%");
                }
            }
        }
    }
}


