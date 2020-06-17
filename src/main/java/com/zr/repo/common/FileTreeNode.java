package com.zr.repo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zr.repo.utils.HttpUtil;
import com.zr.system.common.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.File;
import java.util.*;

import static com.zr.repo.utils.UploadUtil.getSeparatedPath;

/**
 * @Author: 张忍
 * @Date: 2020-03-27 0:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileTreeNode {
    private Integer id;
    private String name;
    private Integer type;
    private Integer parentid;
    private String filepath;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)//为空不渲染到json
    private List<FileTreeNode> child=new ArrayList<>();

    public FileTreeNode(Integer id, String name, Integer type, Integer parentid, String filepath) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentid = parentid;
        this.filepath = filepath;
    }
    /**
     * 结构化List
     */
    public static class FileTreeNodeBuilder{
        public static List<FileTreeNode> build(List<FileTreeNode> treeNodes,Integer topId){
            List<FileTreeNode> nodes = new ArrayList<>();
            for (FileTreeNode n1 : treeNodes) {
                if ((n1.getParentid().equals(topId))){
                    nodes.add(n1);
                }
                for (FileTreeNode n2 : treeNodes) {
                    if(n2.getParentid().equals(n1.getId())){
                        n1.getChild().add(n2);
                    }
                }
            }
            return nodes;
        }

        /**
         * dfs遍历树,生成临时文件夹
         * @param treeNodes 树根节点
         * @param path 临时文件夹路径 /tmp/project9835446/
         */
        public static void FindAllPaths(FileTreeNode treeNodes,String path){

            path += treeNodes.name + "/";
            if (treeNodes.child.size() == 0)
            {
                //文件全路径
                path = path.substring(0,path.lastIndexOf("/")); //去掉后/

                //文件夹处理
                if(treeNodes.getType().equals(Constant.FILE_TYPE_FOLDER)){
                    new File(path).mkdirs();
                }else {
                    //通过url拉取文件到临时文件夹
                    HttpUtil.getNetUrlHttp(treeNodes.getFilepath(),path);
                }
                return;
            }
            //递归遍历树
            for (int i = 0, len = treeNodes.child.size(); i < len; i++)
            {
                FindAllPaths(treeNodes.child.get(i), path);
            }
        }
    }




}
