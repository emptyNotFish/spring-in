package cn.ocoop.shiro.subject;

import java.util.List;

/**
 * Created by liolay on 16-8-11.
 */
public class BasicHttpAuthcUser {
    private User user;
    private List<String> perms;

    public BasicHttpAuthcUser(User user, List<String> perms) {
        this.user = user;
        this.perms = perms;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getPerms() {
        return perms;
    }

    public void setPerms(List<String> perms) {
        this.perms = perms;
    }
}
