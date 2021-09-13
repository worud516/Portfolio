package com.ljk.exam.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 게터세터
@NoArgsConstructor // 인자 없는생성자
@AllArgsConstructor // 생성자
public class Article {
	private int id;
	private String regDate;
	private String updateDate;
	private int memberId;
	private int boardId;
	private String title;
	private String body;
	private int hitCount;
	private int goodReactionPoint;
	private int badReactionPoint;

	private String extra__writerName;
	private boolean extra__actorCanModify;
	private boolean extra__actorCanDelete;

	public String getForPrintType1RegDate() {
		return regDate.substring(2, 16).replace(" ", "<br>");
	}

	public String getForPrintType1UpdateDate() {
		return updateDate.substring(2, 16).replace(" ", "<br>");
	}

	public String getForPrintType2RegDate() {
		return regDate.substring(2, 16);
	}

	public String getForPrintType2UpdateDate() {
		return regDate.substring(2, 16);
	}

	public String getForPrintBody() {
		return body.replaceAll("\n", "<br>");
	}
}