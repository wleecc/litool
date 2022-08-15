package io.leaderli.litool.core.bit;

/**
 * @author leaderli
 * @since 2022/8/16
 */
public class BitPermission {


    // 存储目前的权限状态
    private int state;

    /**
     * 重新设置权限
     */
    public void set(int permission) {
        state = permission;
    }

    /**
     * 添加一项或多项权限
     */
    public void enable(int permission) {
        state |= permission;
    }

    /**
     * 删除一项或多项权限
     */
    public void disable(int permission) {
        state &= ~permission;
    }

    /**
     * 是否拥某些权限
     */
    public boolean allow(int permission) {
        return (state & permission) == permission;
    }

    /**
     * 是否禁用了某些权限
     */
    public boolean notAllow(int permission) {
        return (state & permission) == 0;
    }

    /**
     * 是否仅仅拥有某些权限
     */
    public boolean only(int permission) {
        return state == permission;
    }


    public boolean none() {
        return state == 0;
    }

    public boolean allow() {
        return  !none();
    }
}

