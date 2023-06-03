package com.onejo.seosuri.service.algorithm.saveTemplate;

import com.onejo.seosuri.service.algorithm.ProblemTokenStruct;
import com.onejo.seosuri.service.algorithm.category.Category;
import com.onejo.seosuri.service.algorithm.category.SumDiffCategory;
import com.onejo.seosuri.service.algorithm.category.YXCategory;
import com.onejo.seosuri.service.algorithm.createTemplate.CreateTemplate;
import com.onejo.seosuri.service.algorithm.problem.ProblemValueStruct;

import java.util.ArrayList;

public abstract class SaveAllTemplates {
    protected int[] category_id_ls = new int[] {};
    protected ProblemValueStruct problemValueStruct = new ProblemValueStruct();
    protected CreateTemplate createTemplate;

    ArrayList<Category>[] category_ls_ls;
    Category[] possible_category_ls = new Category[] {new SumDiffCategory(), new YXCategory()};

    ArrayList<Integer>[] sentence_category_id_ls_ls;
    int[] target_sentence_category_ls = new int[] {ProblemTokenStruct.CATEGORY_ID_YX, ProblemTokenStruct.CATEGORY_ID_SUM_DIFFERENCE};
    ArrayList<Integer>[] var_sign_ls_ls;
    public boolean[][] useBoolean_ls_ls;
    private static final boolean[] target_boolean = new boolean[] {true, false};

    public SaveAllTemplates(int[] category_id_ls, ProblemValueStruct problemValueStruct, CreateTemplate createTemplate){
        this.category_id_ls = category_id_ls;
        this.problemValueStruct = problemValueStruct;
        this.createTemplate = createTemplate;
    }

    abstract public void saveAllTemplates();
    abstract public void saveInDB();


    // 순열 메서드(cnt는 선택 횟수)
    public void set_useBoolean_ls_ls(int prob_sentence_num){
        int row_num = (int)Math.pow(2.0f, prob_sentence_num);
        useBoolean_ls_ls = new boolean[row_num][prob_sentence_num];
        for(int i = 0; i < row_num; i++){
            useBoolean_ls_ls[i] = new boolean[prob_sentence_num];
        }
        bool_permutation(0);
    }

    // starts with permutation(0)
    public void bool_permutation(int cnt) {
        int N = useBoolean_ls_ls.length;
        int prob_sentence_num = useBoolean_ls_ls[0].length;
        if (cnt == useBoolean_ls_ls[0].length) {
            return;
        }
        // 대상 집합을 순회하며 숫자를 하나 선택한다.
        for (int i = 0; i < target_boolean.length; i++) {
            // ex) 8개 종류
            // 2개로 나눠 -> 0 ~ N/2-1 : true, N/2 ~ 2N/2-1 : false
            //      cnt = 0     -> 2^1
            // 4개로 나눠 -> 0 ~ N/4-1 : true, N/4 ~ 2N/4-1 : false, 2N/4 ~ 3N/4-1 : true, 3N/4 ~ 4N/4-1 : false
            //      cnt = 1     -> 2^2
            // 8개로 나눠 -> ...
            int offset = N/(int)Math.pow(2, cnt+1);
            for(int j = i * offset; j < N; j += 2 * offset){
                for(int row = j; row < j+offset; row++){
                    useBoolean_ls_ls[row][cnt] = target_boolean[i];
                }
            }
            bool_permutation(cnt + 1);
        }
    }

    public void setSentence_category_id_ls_ls(int prob_sentence_num){
        int row_num = (int)Math.pow(ProblemTokenStruct.SENTENCE_CATEGORY_NUM, prob_sentence_num);
        sentence_category_id_ls_ls = new ArrayList[row_num];   // 모든 순열 리스트
        for(int i = 0; i < row_num; i++){
            sentence_category_id_ls_ls[i] = new ArrayList<Integer>();
        }
        int_permutation(0, sentence_category_id_ls_ls, target_sentence_category_ls, prob_sentence_num);
    }

    protected void setVar_sign_ls_ls(int var_num){
        int row_num = (int)Math.pow(2, var_num);
        var_sign_ls_ls = new ArrayList[row_num];
        for(int i = 0; i < row_num; i++){
            var_sign_ls_ls[i] = new ArrayList<Integer>();
        }
        int_permutation(0, var_sign_ls_ls, new int[] {ProblemTokenStruct.PLUS_SIGN, ProblemTokenStruct.MINUS_SIGN}, var_num);
    }

    protected void setCategory_ls_ls(int prob_sentence_num){
        int row_num = (int)Math.pow(ProblemTokenStruct.SENTENCE_CATEGORY_NUM, prob_sentence_num);
        category_ls_ls = new ArrayList[row_num];   // 모든 순열 리스트
        for(int i = 0; i < row_num; i++){
            category_ls_ls[i] = new ArrayList<Category>();
        }
        category_permutation(0, category_ls_ls, possible_category_ls, prob_sentence_num);
    }

    protected Category[] arrayListToCategoryArray(ArrayList<Category> category_ls){
        Category[] result = new Category[category_ls.size()];
        for(int i = 0; i < category_ls.size(); i++){
            result[i] = category_ls.get(i);
        }
        return result;
    }

    // n^r개 배열 나옴
    // n = target.length
    public void int_permutation(int cnt, ArrayList<Integer>[] dest, int[] target, int r) {
        // target에서 숫자 골라 중복순열 만들기
        // cnt는 현재 탐색 깊이 (depth)
        int n = target.length;
        int N = (int)Math.pow(n, r);
        if (cnt == r) {
            return;
        }
        // 대상 집합을 순회하며 숫자를 하나 선택한다.
        for (int i = 0; i < target.length; i++) {
            // ex) 8개 종류
            // 2개로 나눠 -> 0 ~ N/2-1 : true, N/2 ~ 2N/2-1 : false
            //      cnt = 0     -> 2^1
            // 4개로 나눠 -> 0 ~ N/4-1 : true, N/4 ~ 2N/4-1 : false, 2N/4 ~ 3N/4-1 : true, 3N/4 ~ 4N/4-1 : false
            //      cnt = 1     -> 2^2
            // 8개로 나눠 -> ...
            int offset = N/(int)Math.pow(n, cnt+1);
            for(int j = i * offset; j < N; j += 2 * offset) {
                for (int row = j; row < j + offset; row++) {
                    dest[row].add(target[i]);
                }
            }
        }
        int_permutation(cnt + 1, dest, target, r);
    }

    public void category_permutation(int cnt, ArrayList<Category>[] dest, Category[] target, int r) {
        // target에서 숫자 골라 중복순열 만들기
        // cnt는 현재 탐색 깊이 (depth)
        int n = target.length;
        int N = (int)Math.pow(n, r);
        if (cnt == r) {
            return;
        }
        // 대상 집합을 순회하며 숫자를 하나 선택한다.
        for (int i = 0; i < target.length; i++) {
            // ex) 8개 종류
            // 2개로 나눠 -> 0 ~ N/2-1 : true, N/2 ~ 2N/2-1 : false
            //      cnt = 0     -> 2^1
            // 4개로 나눠 -> 0 ~ N/4-1 : true, N/4 ~ 2N/4-1 : false, 2N/4 ~ 3N/4-1 : true, 3N/4 ~ 4N/4-1 : false
            //      cnt = 1     -> 2^2
            // 8개로 나눠 -> ...
            int offset = N/(int)Math.pow(n, cnt+1);
            for(int j = i * offset; j < N; j += 2 * offset) {
                for (int row = j; row < j + offset; row++) {
                    dest[row].add(target[i]);
                }
            }
        }
        category_permutation(cnt + 1, dest, target, r);
    }

}
