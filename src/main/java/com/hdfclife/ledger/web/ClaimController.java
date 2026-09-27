package com.hdfclife.ledger.web;

import com.hdfclife.ledger.dto.ClaimResponse;
import com.hdfclife.ledger.dto.CreateClaimRequest;
import com.hdfclife.ledger.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/claims")
@Tag(
        name = "Claims",
        description = "HDFC Life claim operations"
)
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * POST /api/claims
     *
     * Creates a new claim for an existing policy.
     */
    @PostMapping
    @Operation(
            summary = "Create a claim",
            description = "Creates a new claim for an existing policy"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Claim created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Policy not found"
    )
    public ResponseEntity<ClaimResponse> createClaim(
            @Valid @RequestBody CreateClaimRequest request) {

        ClaimResponse response =
                claimService.createClaim(request);

        URI location = URI.create(
                "/api/claims/" + response.claimNo()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }
}