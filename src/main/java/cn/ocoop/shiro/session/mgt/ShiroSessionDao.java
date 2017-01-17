package cn.ocoop.shiro.session.mgt;

import cn.ocoop.shiro.session.iface.ShiroSessionRepositoryAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by liolay on 15-7-27.
 */
public class ShiroSessionDao extends AbstractSessionDAO {
    private ShiroSessionRepositoryAware shiroSessionRepositoryAware;

    public ShiroSessionRepositoryAware getShiroSessionRepositoryAware() {
        return shiroSessionRepositoryAware;
    }

    public void setShiroSessionRepositoryAware(ShiroSessionRepositoryAware shiroSessionRepositoryAware) {
        this.shiroSessionRepositoryAware = shiroSessionRepositoryAware;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        getShiroSessionRepositoryAware().saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            return;
        }
        Serializable id = session.getId();
        if (id != null)
            getShiroSessionRepositoryAware().deleteSession(id);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return getShiroSessionRepositoryAware().getAllSessions();
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        getShiroSessionRepositoryAware().saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return getShiroSessionRepositoryAware().getSession(sessionId);
    }


}
