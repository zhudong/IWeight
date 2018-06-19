package com.axecom.iweight.bean;

import java.util.List;

public class ScalesCategoryGoods {
    private List<Parent> parentList;

    public List<Parent> getParentList() {
        return parentList;
    }

    public void setParentList(List<Parent> parentList) {
        this.parentList = parentList;
    }


    class Parent{
        private int id;
        private String name;
        private List<Child> childList;

        public List<Child> getChildList() {
            return childList;
        }

        public void setChildList(List<Child> childList) {
            this.childList = childList;
        }

        class Child{
            private int id;
            private int cid;
            private String name;

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
