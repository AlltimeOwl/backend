package com.owl.payrit.domain.auth.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.owl.payrit.domain.auth.domain.OauthProvider;
import com.owl.payrit.domain.member.entity.Member;
import com.owl.payrit.domain.member.entity.OauthInformation;
import com.owl.payrit.domain.member.entity.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonNaming(SnakeCaseStrategy.class)
public record KakaoOauthResponse(
    Long id,
    boolean hasSignedUp,
    LocalDateTime connectedAt,
    KakaoAccount kakaoAccount

) {
    public Member toEntity() {
        return Member.builder()
                        .oauthInformation(new OauthInformation(String.valueOf(id), OauthProvider.KAKAO, null))
                        .email(kakaoAccount.email)
                        .name(kakaoAccount.name)
                        .phoneNumber(kakaoAccount.phoneNumber)
                        .birthDay(kakaoAccount.birthConverter())
                        .role(Role.MEMBER)
                        .build();
    }

    @JsonNaming(SnakeCaseStrategy.class)
    public record KakaoAccount(
        boolean profileNeedsAgreement,
        boolean profileNicknameNeedsAgreement,
        boolean profileImageNeedsAgreement,
        Profile profile,
        boolean nameNeedsAgreement,
        String name,
        boolean emailNeedsAgreement,
        boolean isEmailValid,
        boolean isEmailVerified,
        String email,
        boolean ageRangeNeedsAgreement,
        String ageRange,
        boolean birthyearNeedsAgreement,
        String birthyear,
        boolean birthdayNeedsAgreement,
        String birthday,
        String birthdayType,
        boolean genderNeedsAgreement,
        String gender,
        boolean phoneNumberNeedsAgreement,
        String phoneNumber,
        boolean ciNeedsAgreement,
        String ci,
        LocalDateTime ciAuthenticatedAt
    ) {
        private static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy");

        public LocalDate birthConverter() {

            if(birthday == null) return null;
            int year = Integer.parseInt(birthyear);
            return LocalDate.parse(birthday + year, BIRTHDAY_FORMATTER);

        }
    }

    @JsonNaming(SnakeCaseStrategy.class)
    public record Profile(
        String nickname,
        String thumbnailImageUrl,
        String profileImageUrl,
        boolean isDefaultImage
    ) {

    }
}
