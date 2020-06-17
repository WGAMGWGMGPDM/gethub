package com.zr.blog.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zr.system.common.MenuTreeNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-04-01 22:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentTreeNode {
    private Integer id;
    private Integer pid;
    private Integer commentUid;
    private Integer atUid;
    private String atName;
    private String commentName;
    private Date commentTime;
    private String commentImg;
    private String commentContent;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)//为空不渲染到json
    private List<CommentTreeNode> child=new ArrayList<>();

    /**
     * 评论节点构造
     * @param id
     * @param pid
     * @param commentUid
     * @param atUid
     * @param atName
     * @param commentName
     * @param commentTime
     * @param commentImg
     * @param commentContent
     */
    public CommentTreeNode(Integer id, Integer pid, Integer commentUid, Integer atUid, String atName, String commentName, Date commentTime, String commentImg, String commentContent) {
        this.id = id;
        this.pid = pid;
        this.commentUid = commentUid;
        this.atUid = atUid;
        this.atName = atName;
        this.commentName = commentName;
        this.commentTime = commentTime;
        this.commentImg = commentImg;
        this.commentContent = commentContent;
    }

    /**
     * 结构化List
     */
    public static class CommentTreeNodeBuilder{
        public static List<CommentTreeNode> build(List<CommentTreeNode> treeNodes,Integer topId){
            List<CommentTreeNode> nodes = new ArrayList<>();
            for (CommentTreeNode n1 : treeNodes) {
                if ((n1.getPid().equals(topId))){
                    nodes.add(n1);
                }
                for (CommentTreeNode n2 : treeNodes) {
                    if(n2.getPid().equals(n1.getId())){
                        n1.getChild().add(n2);
                    }
                }
            }
            return nodes;
        }
    }
}
