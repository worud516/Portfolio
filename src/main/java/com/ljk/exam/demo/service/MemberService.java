package com.ljk.exam.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ljk.exam.demo.repository.MemberRepository;
import com.ljk.exam.demo.util.Ut;
import com.ljk.exam.demo.vo.Member;
import com.ljk.exam.demo.vo.ResultData;

@Service
public class MemberService {
	private MemberRepository memberRepository;
	private AttrService attrService;
	private MailService mailService;
	
	@Value("${custom.siteMainUri}")
    private String siteMainUri;
    @Value("${custom.siteName}")
    private String siteName;

	public MemberService(AttrService attrService, MemberRepository memberRepository, MailService mailService) {
		this.attrService = attrService;
		this.memberRepository = memberRepository;
		this.mailService = mailService;
	}

	public ResultData<Integer> join(String loginId, String loginPw, String name, String nickname, String cellphoneNo,
			String email) {
		// 로그인아이디 중복체크
		Member oldMember = getMemberByLoginId(loginId);

		if (oldMember != null) {
			return ResultData.from("F-7", Ut.f("해당 로그인아이디(%s)는 이미 사용중입니다.", loginId));
		}

		// 이름+이메일 중복체크
		oldMember = getMemberByNameAndEmail(name, email);

		if (oldMember != null) {
			return ResultData.from("F-8", Ut.f("해당 이름(%s)과 이메일(%s)은 이미 사용중입니다.", name, email));
		}

		memberRepository.join(loginId, loginPw, name, nickname, cellphoneNo, email);
		int id = memberRepository.getLastInsertId();

		return ResultData.from("S-1", "회원가입이 완료되었습니다. 다시 로그인 해주세요", "id", id);
	}

	public Member getMemberByNameAndEmail(String name, String email) {
		return memberRepository.getMemberByNameAndEmail(name, email);
	}
	
	public Member getMemberByLoginId(String loginId) {
		return memberRepository.getMemberByLoginId(loginId);
	}

	public Member getMemberById(int id) {
		return memberRepository.getMemberById(id);
	}

	public ResultData modify(int id, String loginPw, String name, String nickname, String email, String cellphoneNo) {

		memberRepository.modify(id, loginPw, name, nickname, email, cellphoneNo);

		return ResultData.from("S-1", "회원정보가 수정되었습니다.");
	}

	public String genMemberModifyAuthKey(int actorId) {
		String memberModifyAuthKey = Ut.getTempPassword(10);

		attrService.setValue("member", actorId, "extra", "memberModifyAuthKey", memberModifyAuthKey,
				Ut.getDateStrLater(60 * 5));

		return memberModifyAuthKey;
	}

	public ResultData checkMemberModifyAuthKey(int actorId, String memberModifyAuthKey) {
		String saved = attrService.getValue("member", actorId, "extra", "memberModifyAuthKey");

		if (!saved.equals(memberModifyAuthKey)) {
			return ResultData.from("F-1", "일치하지 않거나 만료되었습니다.");
		}

		return ResultData.from("S-1", "정상적인 코드입니다.");
	}

	public ResultData notifyTempLoginPwByEmail(Member actor) {
        String title = "[" + siteName + "] 임시 패스워드 발송";
        String tempPassword = Ut.getTempPassword(6);
        String body = "<h1>임시 패스워드 : " + tempPassword + "</h1>";
        body += "<a href=\"" + siteMainUri + "/usr/member/login\" target=\"_blank\">로그인 하러가기</a>";

        ResultData sendResultData = mailService.send(actor.getEmail(), title, body);

        if (sendResultData.isFail()) {
            return sendResultData;
        }

        setTempPassword(actor, tempPassword);

        return ResultData.from("S-1", "계정의 이메일주소로 임시 패스워드가 발송되었습니다.");
    }

	private void setTempPassword(Member actor, String tempPassword) {
		 memberRepository.modify(actor.getId(), tempPassword, null, null, null, null);
	}
}
