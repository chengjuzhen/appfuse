package org.appfuse.dao;

import org.appfuse.Constants;
import org.appfuse.model.Address;
import org.appfuse.model.Role;
import org.appfuse.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

public class UserDaoTest extends BaseDaoTestCase {
    private UserDao dao = null;
    private RoleDao rdao = null;
    
    public void setUserDao(UserDao dao) {
        this.dao = dao;
    }
    
    public void setRoleDao(RoleDao rdao) {
        this.rdao = rdao;
    }

    public void testGetUserInvalid() throws Exception {
        try {
            dao.get(1000L);
            fail("'badusername' found in database, failing test...");
        } catch (DataAccessException d) {
            assertTrue(d != null);
        }
    }

    public void testGetUser() throws Exception {
        User user = dao.get(1L);

        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
    }

    public void testUpdateUser() throws Exception {
        User user = dao.get(1L);

        Address address = user.getAddress();
        address.setAddress("new address");

        user = dao.saveUser(user);

        assertEquals(user.getAddress(), address);
        assertEquals("new address", user.getAddress().getAddress());
        
        // verify that violation occurs when adding new user with same username
        user.setId(null);

        endTransaction();

        try {
            user = dao.saveUser(user);
            fail("saveUser didn't throw DataIntegrityViolationException");
        } catch (DataIntegrityViolationException e) {
            assertNotNull(e);
            log.debug("expected exception: " + e.getMessage());
        }
    }

    public void testAddUserRole() throws Exception {
        User user = dao.get(1L);
        assertEquals(1, user.getRoles().size());

        Role role = rdao.getRoleByName(Constants.ADMIN_ROLE);
        user.addRole(role);
        user = dao.saveUser(user);

        assertEquals(2, user.getRoles().size());

        //add the same role twice - should result in no additional role
        user.addRole(role);
        user = dao.saveUser(user);

        assertEquals("more than 2 roles", 2, user.getRoles().size());

        user.getRoles().remove(role);
        user = dao.saveUser(user);

        assertEquals(1, user.getRoles().size());
    }

    public void testAddAndRemoveUser() throws Exception {
        User user = new User("testuser");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        Address address = new Address();
        address.setCity("Denver");
        address.setProvince("CO");
        address.setCountry("USA");
        address.setPostalCode("80210");
        user.setAddress(address);
        user.setEmail("testuser@appfuse.org");
        user.setWebsite("http://raibledesigns.com");
        
        Role role = rdao.getRoleByName(Constants.USER_ROLE);
        assertNotNull(role.getId());
        user.addRole(role);

        user = dao.saveUser(user);

        assertNotNull(user.getId());
        assertEquals("testpass", user.getPassword());

        dao.remove(user.getId());

        try {
            dao.get(user.getId());
            fail("getUser didn't throw DataAccessException");
        } catch (DataAccessException d) {
            assertNotNull(d);
        }
    }
    
    public void testUserExists() throws Exception {
        boolean b = dao.exists(1L);
        super.assertTrue(b);
    }
    
    public void testUserNotExists() throws Exception {
        boolean b = dao.exists(111L);
        super.assertFalse(b);
    }
}
