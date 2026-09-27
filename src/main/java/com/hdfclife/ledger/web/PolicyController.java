package com.hdfclife.ledger.web;

import com.hdfclife.ledger.dto.ClaimResponse;
import com.hdfclife.ledger.dto.CreatePolicyRequest;
import com.hdfclife.ledger.dto.PolicyResponse;
import com.hdfclife.ledger.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@Tag(
        name = "Policies",
        description = "HDFC Life policy operations"
)
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    /**
     * GET /api/policies
     *
     * Also supports:
     * /api/policies?status=Active
     * /api/policies?type=TERM
     * /api/policies?customer=Anita%20Sharma
     */
    @GetMapping
    @Operation(
            summary = "Get policies",
            description = "Get all policies or filter by status, type, or customer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Policies retrieved successfully"
    )
    public ResponseEntity<List<PolicyResponse>> getPolicies(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String customer) {

        List<PolicyResponse> policies;

        if (status != null) {

            policies = policyService
                    .getPoliciesByStatus(status);

        } else if (type != null) {

            policies = policyService
                    .getPoliciesByType(type);

        } else if (customer != null) {

            policies = policyService
                    .getPoliciesByCustomer(customer);

        } else {

            policies = policyService
                    .getAllPolicies();
        }

        return ResponseEntity.ok(policies);
    }

    /**
     * GET /api/policies/{policyNo}
     */
    @GetMapping("/{policyNo}")
    @Operation(
            summary = "Get policy by policy number"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Policy found"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Policy not found"
    )
    public ResponseEntity<PolicyResponse> getPolicy(
            @PathVariable String policyNo) {

        PolicyResponse response =
                policyService.getPolicyByNumber(policyNo);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/policies/search?minPremium=20000
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search policies by minimum premium"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Policies found"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid minPremium"
    )
    public ResponseEntity<List<PolicyResponse>> searchPolicies(
            @RequestParam(required = false) Integer minPremium) {

        List<PolicyResponse> policies =
                policyService.searchByMinimumPremium(minPremium);

        return ResponseEntity.ok(policies);
    }

    /**
     * POST /api/policies
     */
    @PostMapping
    @Operation(
            summary = "Create a policy"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Policy created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Duplicate policy number"
    )
    public ResponseEntity<PolicyResponse> createPolicy(
            @Valid @RequestBody CreatePolicyRequest request) {

        PolicyResponse response =
                policyService.createPolicy(request);

        URI location = URI.create(
                "/api/policies/" + response.policyNo()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    /**
     * DELETE /api/policies/{policyNo}
     */
    @DeleteMapping("/{policyNo}")
    @Operation(
            summary = "Delete a policy"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Policy deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Policy not found"
    )
    public ResponseEntity<Void> deletePolicy(
            @PathVariable String policyNo) {

        policyService.deletePolicy(policyNo);

        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/policies/{policyNo}/claims
     */
    @GetMapping("/{policyNo}/claims")
    @Operation(
            summary = "Get claims for a policy"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Claims retrieved successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Policy not found"
    )
    public ResponseEntity<List<ClaimResponse>> getPolicyClaims(
            @PathVariable String policyNo) {

        List<ClaimResponse> claims =
                policyService.getClaimsForPolicy(policyNo);

        return ResponseEntity.ok(claims);
    }
}