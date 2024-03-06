package com.owl.payrit.domain.promissorypaper.service;

import com.owl.payrit.domain.auth.dto.response.LoginUser;
import com.owl.payrit.domain.member.entity.Member;
import com.owl.payrit.domain.member.exception.MemberException;
import com.owl.payrit.domain.member.service.MemberService;
import com.owl.payrit.domain.promissorypaper.dto.request.PaperWriteRequest;
import com.owl.payrit.domain.promissorypaper.dto.response.CreditorPaperResponse;
import com.owl.payrit.domain.promissorypaper.dto.response.PaperDetailResponse;
import com.owl.payrit.domain.promissorypaper.entity.PromissoryPaper;
import com.owl.payrit.domain.promissorypaper.exception.PromissoryPaperException;
import com.owl.payrit.domain.promissorypaper.repository.PromissoryPaperRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.owl.payrit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromissoryPaperService {

    private final MemberService memberService;
    private final PromissoryPaperRepository promissoryPaperRepository;

    @Transactional
    public void writePaper(LoginUser loginUser, PaperWriteRequest paperWriteRequest) {

        Member loginedMember = memberService.findById(loginUser.id());

        //FIXME: 상대방이 회원가입 하지 않은 상황이면, 조회가 불가능? => 일단 null로 반환함.
        Member creditor = memberService.findByPhoneNumberForPromissory(paperWriteRequest.creditorPhoneNumber()).orElse(null);
        Member debtor = memberService.findByPhoneNumberForPromissory(paperWriteRequest.debtorPhoneNumber()).orElse(null);

        //TODO: 폼 입력 데이터와 사전 입력 데이터가 일치하는지 검사 필요??

        PromissoryPaper paper = PromissoryPaper.builder()
                .amount(paperWriteRequest.amount())
                .transactionDate(paperWriteRequest.transactionDate())
                .repaymentStartDate(paperWriteRequest.repaymentStartDate())
                .repaymentEndDate(paperWriteRequest.repaymentEndDate())
                .specialConditions(paperWriteRequest.specialConditions())
                .interestRate(paperWriteRequest.interestRate())
                .writer(loginedMember)
                .creditor(creditor)
                .creditorPhoneNumber(paperWriteRequest.creditorPhoneNumber())
                .creditorAddress(paperWriteRequest.creditorAddress())
                .isCreditorAgree(loginedMember.equals(creditor))
                .debtor(debtor)
                .debtorPhoneNumber(paperWriteRequest.debtorPhoneNumber())
                .debtorAddress(paperWriteRequest.debtorAddress())
                .isDebtorAgree(loginedMember.equals(debtor))
                .paperKey(getRandomKey())
                .storageUrl(null)           //FIXME: 추후 저장소 URL로 저장 필요
                .build();

        promissoryPaperRepository.save(paper);
    }

    private String getRandomKey() {

        String paperKey = UUID.randomUUID().toString();
        return promissoryPaperRepository.existsByPaperKey(paperKey) ? getRandomKey() : paperKey;
    }

    public PaperDetailResponse getDetail(LoginUser loginUser, Long paperId) {

        PromissoryPaper promissoryPaper = promissoryPaperRepository.findById(paperId).orElseThrow(
                () -> new PromissoryPaperException(ErrorCode.PAPER_NOT_FOUND));

        if(!isMine(loginUser.id(), promissoryPaper)) {
            throw new PromissoryPaperException(ErrorCode.PAPER_IS_NOT_MINE);
        }

        return new PaperDetailResponse(promissoryPaper);
    }

    private boolean isMine(Long memberId, PromissoryPaper promissoryPaper) {

        if(promissoryPaper.getCreditor().getId().equals(memberId)
                || promissoryPaper.getDebtor().getId().equals(memberId)) {
            return true;
        }

        return false;
    }

    public List<CreditorPaperResponse> getCreditorPaperList(LoginUser loginUser) {

        Member loginedMember = memberService.findById(loginUser.id());

        List<PromissoryPaper> creditorPaperList = promissoryPaperRepository.findAllByCreditor(loginedMember);

        //FIXME: 없으면 null 인 상태로라도 리스트를 전달해야 프론트에서 처리? or exception 발생?

        List<CreditorPaperResponse> creditorPaperResponseList = new ArrayList<>();
        for(PromissoryPaper paper : creditorPaperList) {
            creditorPaperResponseList.add(new CreditorPaperResponse(paper));
        }
        
        //TODO: Creditor과 Debtor을 한번에 처리할만한 방법 있을지 모색 필요

        return creditorPaperResponseList;
    }
}