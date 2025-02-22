package com.owl.payrit.domain.transactionhistory.repository;

import com.owl.payrit.domain.member.entity.Member;
import com.owl.payrit.domain.transactionhistory.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    Optional<TransactionHistory> findByMerchantUid(String merchantUid);
    Optional<TransactionHistory> findByApplyNum(String applyNum);
    Optional<TransactionHistory> findByImpUid(String impUid);
    Optional<TransactionHistory> findByBuyerNameAndBuyerPhone(String buyerName, String buyerPhone);
    List<TransactionHistory> findAllByBuyerNameAndBuyerPhone(String buyerName, String buyerPhone);
}
