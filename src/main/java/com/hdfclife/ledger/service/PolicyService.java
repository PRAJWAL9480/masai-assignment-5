package com.hdfclife.ledger.service;

import com.hdfclife.ledger.domain.Customer;
import com.hdfclife.ledger.domain.Policy;
import com.hdfclife.ledger.domain.Rider;
import com.hdfclife.ledger.dto.ClaimResponse;
import com.hdfclife.ledger.dto.CreatePolicyRequest;
import com.hdfclife.ledger.dto.PolicyResponse;
import com.hdfclife.ledger.exception.DuplicatePolicyException;
import com.hdfclife.ledger.exception.InvalidRequestException;
import com.hdfclife.ledger.exception.PolicyNotFoundException;
import com.hdfclife.ledger.repo.ClaimRepository;
import com.hdfclife.ledger.repo.CustomerRepository;
import com.hdfclife.ledger.repo.PolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final ClaimRepository claimRepository;

    public PolicyService(
            PolicyRepository policyRepository,
            CustomerRepository customerRepository,
            ClaimRepository claimRepository) {

        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.claimRepository = claimRepository;
    }

    /**
     * Create a new policy.
     *
     * If the email already exists, reuse the existing customer.
     * If the email is new, create a new customer.
     *
     * Duplicate policy number -> 409
     */
    @Transactional
    public PolicyResponse createPolicy(CreatePolicyRequest request) {

        // Check duplicate policy number
        if (policyRepository.existsByPolicyNo(request.policyNo())) {
            throw new DuplicatePolicyException(request.policyNo());
        }

        // Find existing customer by email or create a new one
        Customer customer = customerRepository
                .findByEmail(request.email())
                .orElseGet(() -> customerRepository.save(
                        new Customer(
                                request.customer(),
                                request.email()
                        )
                ));

        // Create policy
        Policy policy = new Policy(
                request.policyNo(),
                customer,
                request.type(),
                request.basePremium(),
                request.status()
        );

        // Save policy
        Policy savedPolicy = policyRepository.save(policy);

        return toPolicyResponse(savedPolicy);
    }

    /**
     * Get all policies.
     *
     * Default order:
     * policy number ascending.
     */
    @Transactional
    public List<PolicyResponse> getAllPolicies() {

        return policyRepository.findAll()
                .stream()
                .sorted((p1, p2) ->
                        p1.getPolicyNo()
                                .compareTo(p2.getPolicyNo()))
                .map(this::toPolicyResponse)
                .toList();
    }

    /**
     * Get one policy by policy number.
     */
    @Transactional
    public PolicyResponse getPolicyByNumber(String policyNo) {

        Policy policy = policyRepository
                .findByPolicyNo(policyNo)
                .orElseThrow(() ->
                        new PolicyNotFoundException(policyNo));

        return toPolicyResponse(policy);
    }

    /**
     * Get policies by status.
     *
     * Uses the required derived repository method.
     */
    @Transactional
    public List<PolicyResponse> getPoliciesByStatus(String status) {

        return policyRepository
                .findByStatusOrderByPolicyNoAsc(status)
                .stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    /**
     * Get policies by product type.
     *
     * Uses the required derived repository method.
     */
    @Transactional
    public List<PolicyResponse> getPoliciesByType(String type) {

        return policyRepository
                .findByProductTypeOrderByPolicyNoAsc(type)
                .stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    /**
     * Get policies belonging to a customer.
     *
     * Uses the required derived repository method.
     */
    @Transactional
    public List<PolicyResponse> getPoliciesByCustomer(
            String customerName) {

        return policyRepository
                .findByCustomer_FullNameOrderByPolicyNoAsc(customerName)
                .stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    /**
     * Search policies whose premium is >= minPremium.
     *
     * Uses the required JPQL query.
     */
    @Transactional
    public List<PolicyResponse> searchByMinimumPremium(
            Integer minPremium) {

        if (minPremium == null || minPremium < 0) {
            throw new InvalidRequestException(
                    "minPremium must be >= 0"
            );
        }

        return policyRepository
                .findWithPremiumAtLeast(minPremium)
                .stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    /**
     * Delete a policy by policy number.
     *
     * If the policy doesn't exist -> 404.
     */
    @Transactional
    public void deletePolicy(String policyNo) {

        Policy policy = policyRepository
                .findByPolicyNo(policyNo)
                .orElseThrow(() ->
                        new PolicyNotFoundException(policyNo));

        policyRepository.delete(policy);
    }

    /**
     * Get all claims belonging to a policy.
     *
     * First verifies that the policy exists.
     */
    @Transactional
    public List<ClaimResponse> getClaimsForPolicy(
            String policyNo) {

        // Verify policy exists
        policyRepository
                .findByPolicyNo(policyNo)
                .orElseThrow(() ->
                        new PolicyNotFoundException(policyNo));

        return claimRepository
                .findByPolicy_PolicyNoOrderByClaimNoAsc(policyNo)
                .stream()
                .map(claim -> new ClaimResponse(
                        claim.getClaimNo(),
                        claim.getPolicy().getPolicyNo(),
                        claim.getClaimAmount(),
                        claim.getUrgency(),
                        claim.getStatus()
                ))
                .toList();
    }

    /**
     * Convert Policy entity to PolicyResponse DTO.
     *
     * Controllers should return DTOs rather than entities.
     */
    private PolicyResponse toPolicyResponse(Policy policy) {

        List<String> riderCodes = policy.getRiders()
                .stream()
                .map(Rider::getCode)
                .sorted()
                .toList();

        return new PolicyResponse(
                policy.getPolicyNo(),
                policy.getCustomer().getFullName(),
                policy.getCustomer().getEmail(),
                policy.getProductType(),
                policy.getBasePremium(),
                policy.getStatus(),
                riderCodes
        );
    }
}