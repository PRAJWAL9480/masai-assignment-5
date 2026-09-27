package com.hdfclife.ledger.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_no", nullable = false, unique = true, length = 30)
    private String claimNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(name = "claim_amount", nullable = false)
    private int claimAmount;

    @Column(nullable = false, length = 20)
    private String urgency;

    @Column(nullable = false, length = 20)
    private String status;

    public Long getId() {
        return id;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public int getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(int claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public Claim(String claimNo, Policy policy, int claimAmount, String urgency, String status) {
		super();
		this.claimNo = claimNo;
		this.policy = policy;
		this.claimAmount = claimAmount;
		this.urgency = urgency;
		this.status = status;
	}

	public Claim() {
		super();
	}
	
}