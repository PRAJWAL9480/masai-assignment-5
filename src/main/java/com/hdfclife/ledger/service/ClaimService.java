package com.hdfclife.ledger.service;

import com.hdfclife.ledger.domain.Claim;
import com.hdfclife.ledger.domain.Policy;
import com.hdfclife.ledger.dto.ClaimResponse;
import com.hdfclife.ledger.dto.CreateClaimRequest;
import com.hdfclife.ledger.exception.ClaimNotFoundException;
import com.hdfclife.ledger.exception.PolicyNotFoundException;
import com.hdfclife.ledger.repo.ClaimRepository;
import com.hdfclife.ledger.repo.PolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    public ClaimService(
            ClaimRepository claimRepository,
            PolicyRepository policyRepository) {

        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    /**
     * Create a new claim for an existing policy.
     */
    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest request) {

        // Find the policy
        Policy policy = policyRepository
                .findByPolicyNo(request.policyNo())
                .orElseThrow(() ->
                        new PolicyNotFoundException(request.policyNo()));

        // Generate next claim number
        String claimNo = generateNextClaimNumber();

        // Create claim
        Claim claim = new Claim(
                claimNo,
                policy,
                request.claimAmount(),
                request.urgency(),
                "SUBMITTED"
        );

        // Save claim
        Claim savedClaim = claimRepository.save(claim);

        return toClaimResponse(savedClaim);
    }

    /**
     * Find a claim by claim number.
     */
    @Transactional
    public ClaimResponse getClaimByNumber(String claimNo) {

        Claim claim = claimRepository
                .findByClaimNo(claimNo)
                .orElseThrow(() ->
                        new ClaimNotFoundException(claimNo));

        return toClaimResponse(claim);
    }

    /**
     * Generate claim number:
     *
     * CLM-01
     * CLM-02
     * CLM-03
     * ...
     */
    private String generateNextClaimNumber() {

        long count = claimRepository.count();

        long nextNumber = count + 1;

        return String.format("CLM-%02d", nextNumber);
    }

    /**
     * Convert Claim entity to ClaimResponse DTO.
     */
    private ClaimResponse toClaimResponse(Claim claim) {

        return new ClaimResponse(
                claim.getClaimNo(),
                claim.getPolicy().getPolicyNo(),
                claim.getClaimAmount(),
                claim.getUrgency(),
                claim.getStatus()
        );
    }
}