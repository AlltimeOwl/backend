package com.owl.payrit.domain.transactionhistory.dto.response;

public record PaymentInfoResponse(

        String PID,
        String PGCode,
        String merchantUID,
        String name,
        int amount,
        String buyerEmail,
        String buyerName,
        String buyerTel

) {
}
