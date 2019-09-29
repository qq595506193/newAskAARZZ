package com.dmsj.newask.page_scroll;

/**
 * Created by TriumphalSun
 * on 2019/9/29 0029
 * com.dmsj.newask.page_scroll name of the package in which the new file is created
 */
public interface PageDecorationLastJudge {
    /**
     * Is the last row in one page
     *
     * @param position
     * @return
     */
    boolean isLastRow(int position);

    /**
     * Is the last Colum in one row;
     *
     * @param position
     * @return
     */
    boolean isLastColumn(int position);

    boolean isPageLast(int position);
}
