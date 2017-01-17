package cn.ocoop.shiro.session.iface;

import org.apache.shiro.session.Session;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by liolay on 15-7-27.
 */
public interface ShiroSessionRepositoryAware {
    void saveSession(Session session);

    void deleteSession(Serializable sessionId);

    Session getSession(Serializable sessionId);

    Collection<Session> getAllSessions();

}
