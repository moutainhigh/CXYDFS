/*
author:chxy
data:2020/5/24
description:
*/
package miscellaneous;

public abstract class AbstractHandler implements Handler,Runnable,Cloneable{

    protected Message msg;

    public void setMsg(Message msg){
        this.msg = msg;
    }

    @Override
    //公共克隆方法
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}


