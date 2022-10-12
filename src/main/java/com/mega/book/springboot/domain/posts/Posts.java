package com.mega.book.springboot.service.domain.posts;

import com.mega.book.springboot.service.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Posts extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키를 데이터베이스에 위임(auto 와 비슷한 개념)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false) //columnDefinition = "TEXT" -> 긴 글 작성 시
    private String content;

    private String author;

    @Builder //빌드패턴 -> 객체를 생성하는 클래스와 표현하는 클래스 분리
    public Posts(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}
