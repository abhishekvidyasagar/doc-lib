package com.doconline.doconline.helper;

public class PaginationModel {

    private int currentPage;
    private int nextPage;
    private int perPage;
    private int prevPage;
    private int total;

    public int getCurrentPage()
    {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public int getNextPage()
    {
        return this.nextPage;
    }

    public void setNextPage(int nextPage)
    {
        this.nextPage = nextPage;
    }

    public int getPerPage()
    {
        return this.perPage;
    }

    public void setPerPage(int perPage)
    {
        this.perPage = perPage;
    }

    public int getPrevPage()
    {
        return this.prevPage;
    }

    public void setPrevPage(int prevPage)
    {
        this.prevPage = prevPage;
    }

    public int getTotal()
    {
        return this.total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }
}