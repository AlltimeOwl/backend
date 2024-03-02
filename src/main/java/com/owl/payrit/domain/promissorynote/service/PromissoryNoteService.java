package com.owl.payrit.domain.promissorynote.service;

import com.owl.payrit.domain.promissorynote.dto.request.PaperWriteRequest;
import com.owl.payrit.domain.promissorynote.entity.PromissoryNote;
import com.owl.payrit.domain.promissorynote.repository.PromissoryNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromissoryNoteService {

    private final PromissoryNoteRepository promissoryNoteRepository;

    @Transactional
    public void createNote(PaperWriteRequest paperWriteRequest) {

        //FIXME: 로그인한 사용자 가져오기 (Principal -> Member)

        /*TODO
        빌려줄 예정이에요 OR 빌릴 예정이에요
        -> 내 정보 입력, 상대방 정보 입력
        작성자가 어떤 역할이냐에 따라 로그인한 사용자가 creditor OR debtor */

        PromissoryNote promissoryNote = PromissoryNote.builder()
                .amount(paperWriteRequest.amount())
                .transactionDate(paperWriteRequest.transactionDate())
                .repaymentStartDate(paperWriteRequest.repaymentStartDate())
                .repaymentEndDate(paperWriteRequest.repaymentEndDate())
                .specialConditions(paperWriteRequest.specialConditions())
                .creditorPhoneNumber(paperWriteRequest.creditorPhoneNumber())
                .creditorAddress(paperWriteRequest.creditorAddress())
                .debtorPhoneNumber(paperWriteRequest.debtorPhoneNumber())
                .debtorAddress(paperWriteRequest.debtorAddress())
                .build();

        promissoryNoteRepository.save(promissoryNote);
    }
}