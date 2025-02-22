package com.owl.payrit.domain.member.service;

import com.owl.payrit.domain.auth.dto.response.LoginUser;
import com.owl.payrit.domain.member.dto.response.CertificationResponse;
import com.owl.payrit.domain.member.dto.response.StatusResponse;
import com.owl.payrit.domain.member.entity.Member;
import com.owl.payrit.domain.member.entity.OauthInformation;
import com.owl.payrit.domain.member.exception.MemberErrorCode;
import com.owl.payrit.domain.member.exception.MemberException;
import com.owl.payrit.domain.member.repository.MemberRepository;
import com.owl.payrit.domain.promise.entity.Promise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findById(long id) {

        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findByPhoneNumber(String phoneNumber) {

        return memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }


    public Optional<Member> findByPhoneNumberForPromissory(String phoneNumber) {

        //NOTE: 차용증 작성 시, 상대방이 가입되어있지 않을 상황을 고려해 Optional<Member> 반환

        return memberRepository.findByCertificationInformationPhone(phoneNumber);
    }

    public Member findByOauthInformation(OauthInformation oauthInformation) {
        return memberRepository.findByOauthInformation(oauthInformation)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findByOauthInformationOrSave(Member member) {
        return memberRepository.findByOauthInformationOauthProviderIdAndOauthInformationOauthProvider(member.getOauthInformation().getOauthProviderId(), member.getOauthInformation().getOauthProvider())
                .orElseGet(() -> memberRepository.save(member));
    }

    public Member findByOauthDetailInformation(OauthInformation oauthInformation) {
        return memberRepository.findByOauthInformationOauthProviderIdAndOauthInformationOauthProvider(oauthInformation.getOauthProviderId(), oauthInformation.getOauthProvider())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void modifyAlarmStatus(LoginUser loginUser) {
        Member member = findByOauthDetailInformation(loginUser.oauthInformation());
        member.modifyAlarmStatus();
    }

    public void delete(Member member) {
        memberRepository.delete(member);
    }

    public boolean existsByCertificationInformation(String name, String phone) {
        return memberRepository.existsByCertificationInformationNameAndCertificationInformationPhone(name, phone);
    }

    public CertificationResponse findCertificationInformation(LoginUser loginUser) {
        Member member = findByOauthDetailInformation(loginUser.oauthInformation());
        return CertificationResponse.of(member);
    }

    public StatusResponse getStatus(LoginUser loginUser) {
        Member member = findByOauthDetailInformation(loginUser.oauthInformation());
        return StatusResponse.of(member);
    }

    public String getWriterNameByMemberForPromise(Promise promise, Member member) {

        Member writer = promise.getWriter();
        boolean isWriter = promise.getWriter().equals(member);
        boolean isMemberAuthenticated = member.isAuthenticated();
        boolean isWriterAuthenticated = writer.isAuthenticated();

        if (!isMemberAuthenticated && isWriter) {
            return "나";
        } else if (isMemberAuthenticated && isWriter) {
            return member.getCertificationInformation().getName();
        } else if (!isWriterAuthenticated && !isWriter) {
            return "익명의 작성자";
        } else if (isWriterAuthenticated && !isWriter) {
            return writer.getCertificationInformation().getName();
        }

        return "알 수 없는 작성자";
    }
}
