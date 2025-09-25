package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.service.MeetingMinutePreparationService;
import com.sep.mmms_backend.utils.NumberUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
public class MeetingMinuteController {

        private final MeetingMinutePreparationService meetingMinutePreparationService;
        public MeetingMinuteController(MeetingMinutePreparationService meetingMinutePreparationService) {
            this.meetingMinutePreparationService = meetingMinutePreparationService;
        }
        @GetMapping("api/previewMeetingMinute")
        public Object holiday(Model model,
                              @RequestParam int committeeId,
                              @RequestParam int meetingId,
                              @RequestParam(required=false) String download,
                              Authentication authentication)
                {
            try {

                String templateName ="meeting_minute_nepali";

                Map<String, Object> meetingMinuteData = meetingMinutePreparationService.prepareMeetingMinuteData(committeeId, meetingId, authentication.getName());
                meetingMinuteData.put("numUtils", new NumberUtils());

                if(download != null && download.equalsIgnoreCase("docx")) {
                    meetingMinuteData.put("download", true);
                    String htmlContent = meetingMinutePreparationService.renderHtmlTemplate(templateName, meetingMinuteData);
                    byte[] docxBytes = meetingMinutePreparationService.createWordDocumentFromHtml(htmlContent);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
                    String filename = "MeetingMinutes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".docx";
                    headers.setContentDispositionFormData("attachment", filename);
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(docxBytes, headers, HttpStatus.OK);
                }

                meetingMinuteData.put("download", false);
                model.addAllAttributes(meetingMinuteData);
                return templateName;
            } catch (Exception e) {
                return "committee_not_accessible";
            }
        }
}
