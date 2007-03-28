package org.appfuse.dao.ibatis;

import java.util.List;

import org.appfuse.dao.RoleDao;
import org.appfuse.model.Role;

/**
 * This class interacts with iBatis's SQL Maps to save/delete and
 * retrieve Role objects.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class RoleDaoiBatis extends GenericDaoiBatis<Role, Long> implements RoleDao {
    
    public RoleDaoiBatis() {
        super(Role.class);
    }

    @Override
    public List getAll() {
        return getSqlMapClientTemplate().queryForList("getRoles", null);
    }
    
    public Role getRoleByName(String name) {
        return (Role) getSqlMapClientTemplate().queryForObject("getRoleByName", name);
    }

    @Override
    public Role save(final Role role) {
        if (role.getId() == null) {
            getSqlMapClientTemplate().update("addRole", role);
        } else {
            getSqlMapClientTemplate().update("updateRole", role);
        }
        return role;
    }

    public void removeRole(String rolename) {
        getSqlMapClientTemplate().update("deleteRole", rolename);
    }

}
