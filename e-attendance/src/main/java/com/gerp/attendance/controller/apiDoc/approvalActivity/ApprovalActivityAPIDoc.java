package com.gerp.attendance.controller.apiDoc.approvalActivity;

import com.gerp.shared.enums.TableEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Approval Activity Log", description = "This API is used to get activity log")
public interface ApprovalActivityAPIDoc {

    @Operation(summary = "Get All Approval Activity Log",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "",
            tags = {"Approval Activity Log"}
//            ,
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
//                    @Content(schema = @Schema(name = "customSchema"), examples =
//                    @ExampleObject(value = "{\n" +
//                            "  \"userId\":\"int\",\n" +
//                            "  \"ids\": int[]\n" +
//                            "}"))
//            })
    )
    ResponseEntity<?> get(@PathVariable TableEnum type, @PathVariable Long id);
}
