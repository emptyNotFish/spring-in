package cn.ocoop.shiro.authc.realm.resolves.delegate;

import cn.ocoop.shiro.subject.BasicHttpAuthcUser;

import java.util.Collection;

public interface HttpBasicSubjectAware {
    Collection<BasicHttpAuthcUser> getUsers();
}
