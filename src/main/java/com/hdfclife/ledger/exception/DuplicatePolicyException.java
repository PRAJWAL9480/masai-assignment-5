package com.hdfclife.ledger.exception;
public class DuplicatePolicyException extends DeskException {

    public DuplicatePolicyException(String policyNo) {
        super("Policy already exists: " + policyNo);
    }
}