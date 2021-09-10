package com.ljk.exam.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ljk.exam.demo.service.MemberService;
import com.ljk.exam.demo.util.Ut;
import com.ljk.exam.demo.vo.Member;
import com.ljk.exam.demo.vo.ResultData;
import com.ljk.exam.demo.vo.Rq;

@Controller
public class UsrMemberController {
	private MemberService memberService;
	private Rq rq;

	public UsrMemberController(MemberService memberService, Rq rq) {
		this.memberService = memberService;
		this.rq = rq;
	}

	@RequestMapping("/usr/member/join")
	public String showJoin() {
		return "usr/member/join";
	}

	@RequestMapping("/usr/member/doJoin")
	@ResponseBody
	public String doJoin(String loginId, String loginPw, String name, String nickname, String cellphoneNo,
			String email) {
		if (Ut.empty(loginId)) {
			return rq.jsHistoryBack("loginId(을)를 입력해주세요.");
		}

		if (Ut.empty(loginPw)) {
			return rq.jsHistoryBack("loginPw(을)를 입력해주세요.");
		}

		if (Ut.empty(name)) {
			return rq.jsHistoryBack("name(을)를 입력해주세요.");
		}

		if (Ut.empty(nickname)) {
			return rq.jsHistoryBack("nickname(을)를 입력해주세요.");
		}

		if (Ut.empty(cellphoneNo)) {
			return rq.jsHistoryBack("cellphoneNo(을)를 입력해주세요.");
		}

		if (Ut.empty(email)) {
			return rq.jsHistoryBack("email(을)를 입력해주세요.");
		}

		ResultData<Integer> joinRd = memberService.join(loginId, loginPw, name, nickname, cellphoneNo, email);

		if (joinRd.isFail()) {
			return rq.jsHistoryBack(Ut.f("%s", joinRd.getMsg()));
		}

		Member member = memberService.getMemberById(joinRd.getData1());

		return rq.jsReplace(Ut.f("%s", joinRd.getMsg()), "/usr/member/login");
	}

	@RequestMapping("/usr/member/doLogout")
	@ResponseBody
	public String doLogout() {
		rq.logout();

		return rq.jsReplace("로그아웃 되었습니다.", "/");
	}

	@RequestMapping("/usr/member/login")
	public String showLogin() {
		return "usr/member/login";
	}

	@RequestMapping("/usr/member/findLoginId")
	public String showFindLoginId() {
		return "usr/member/findLoginId";
	}

    @RequestMapping("/usr/member/doFindLoginId")
    @ResponseBody
    public String doFindLoginId(String name, String email, String replaceUri) {
        if (Ut.empty(replaceUri)) {
        	replaceUri = "/";
        }

        Member member = memberService.getMemberByNameAndEmail(name, email);

        if (member == null) {
            return rq.jsHistoryBack("일치하는 회원이 존재하지 않습니다.");
        }

        return rq.jsReplace(Ut.f("회원님의 아이디는 `%s` 입니다.", member.getLoginId()), replaceUri);
    }
    
    @RequestMapping("/usr/member/findLoginPw")
	public String showFindLoginPw() {
		return "usr/member/findLoginPw";
	}

    @RequestMapping("/usr/member/doFindLoginPw")
    @ResponseBody
    public String doFindLoginPw(String loginId, String name, String email, String replaceUri) {
        if (Ut.empty(replaceUri)) {
        	replaceUri = "/";
        }

        Member member = memberService.getMemberByLoginId(loginId);

        if (member == null) {
            return rq.jsHistoryBack("일치하는 회원이 존재하지 않습니다.");
        }
        
        if (member.getName().equals(name) == false) {
            return rq.jsHistoryBack("일치하는 회원이 존재하지 않습니다.");
        }

        if (member.getEmail().equals(email) == false) {
            return rq.jsHistoryBack("일치하는 회원이 존재하지 않습니다.");
        }
        
        ResultData notifyTempLoginPwByEmailRs = memberService.notifyTempLoginPwByEmail(member);
        
        return rq.jsReplace(Ut.f("%s", notifyTempLoginPwByEmailRs.getMsg()), replaceUri);
    }

	@RequestMapping("/usr/member/doLogin")
	@ResponseBody
	public String doLogin(String loginId, String loginPw) {
		if (Ut.empty(loginId)) {
			return rq.jsHistoryBack("loginId(을)를 입력해주세요.");
		}

		if (Ut.empty(loginPw)) {
			return rq.jsHistoryBack("loginPw(을)를 입력해주세요.");
		}

		Member member = memberService.getMemberByLoginId(loginId);

		if (member == null) {
			return rq.jsHistoryBack("존재하지 않은 로그인아이디 입니다.");
		}

		if (member.getLoginPw().equals(loginPw) == false) {
			return rq.jsHistoryBack("비밀번호가 일치하지 않습니다.");
		}

		rq.login(member);

		return rq.jsReplace(Ut.f("%s님 환영합니다.", member.getNickname()), "/");
	}

	@RequestMapping("/usr/member/myPage")
	public String showMyPage() {
		return "usr/member/myPage";
	}

	@RequestMapping("/usr/member/checkPassword")
	public String showCheckPassword() {
		return "usr/member/checkPassword";
	}

	@RequestMapping("/usr/member/doCheckPassword")
	@ResponseBody
	public String doCheckPassword(String loginPw, String replaceUri) {
		if (Ut.empty(loginPw)) {
			return rq.jsHistoryBack("loginPw(을)를 입력해주세요.");
		}

		if (rq.getLoginedMember().getLoginPw().equals(loginPw) == false) {
			return rq.jsHistoryBack("비밀번호가 일치하지 않습니다.");
		}

		if (replaceUri.equals("../member/modify")) {
			String memberModifyAuthKey = memberService.genMemberModifyAuthKey(rq.getLoginedMemberId());

			replaceUri += "?memberModifyAuthKey=" + memberModifyAuthKey;
		}

		return rq.jsReplace("", replaceUri);
	}

	@RequestMapping("/usr/member/modify")
	public String showModify(String memberModifyAuthKey) {
		if (Ut.empty(memberModifyAuthKey)) {
			return rq.historyBackJsOnView("memberModifyAuthKey(이)가 필요합니다.");
		}

		ResultData checkMemberModifyAuthKeyRd = memberService.checkMemberModifyAuthKey(rq.getLoginedMemberId(),
				memberModifyAuthKey);

		if (checkMemberModifyAuthKeyRd.isFail()) {
			return rq.historyBackJsOnView(checkMemberModifyAuthKeyRd.getMsg());
		}

		return "usr/member/modify";
	}

	@RequestMapping("/usr/member/doModify")
	@ResponseBody
	public String doModify(String memberModifyAuthKey, String loginPw, String name, String nickname, String email,
			String cellphoneNo) {
		if (Ut.empty(memberModifyAuthKey)) {
			return rq.jsHistoryBack("memberModifyAuthKey(이)가 필요합니다.");
		}

		ResultData checkMemberModifyAuthKeyRd = memberService.checkMemberModifyAuthKey(rq.getLoginedMemberId(),
				memberModifyAuthKey);

		if (checkMemberModifyAuthKeyRd.isFail()) {
			return rq.jsHistoryBack(checkMemberModifyAuthKeyRd.getMsg());
		}

		if (Ut.empty(loginPw)) {
			loginPw = null;
		}

		if (Ut.empty(name)) {
			return rq.jsHistoryBack("name(을)를 입력해주세요.");
		}

		if (Ut.empty(nickname)) {
			return rq.jsHistoryBack("nickname(을)를 입력해주세요.");
		}

		if (Ut.empty(email)) {
			return rq.jsHistoryBack("email(을)를 입력해주세요.");
		}

		if (Ut.empty(cellphoneNo)) {
			return rq.jsHistoryBack("cellphoneNo(을)를 입력해주세요.");
		}

		ResultData modifyRd = memberService.modify(rq.getLoginedMemberId(), loginPw, name, nickname, email,
				cellphoneNo);

		return rq.jsReplace(modifyRd.getMsg(), "/");
	}
}