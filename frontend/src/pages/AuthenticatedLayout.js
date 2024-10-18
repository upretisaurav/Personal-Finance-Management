import React from 'react';
import { Link, Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Box, Drawer, List, ListItem, ListItemIcon, ListItemText, AppBar, Toolbar, Typography, Divider } from '@mui/material';
import { Dashboard, AccountBalance, Receipt, MonetizationOn, ExitToApp } from '@mui/icons-material';

const drawerWidth = 250;

const AuthenticatedLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    navigate('/login');
  };

  const menuItems = [
    { text: 'Dashboard', icon: <Dashboard />, link: '/dashboard' },
    { text: 'Budgets', icon: <AccountBalance />, link: '/budgets' },
    { text: 'Expenses', icon: <Receipt />, link: '/expenses' },
    { text: 'Investments', icon: <MonetizationOn />, link: '/investments' },
  ];

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar>
          <Typography variant="h6" noWrap component="div">
            Personal Finance Manager
          </Typography>
        </Toolbar>
      </AppBar>
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          ['& .MuiDrawer-paper']: { width: drawerWidth, boxSizing: 'border-box' },
        }}
      >
        <Toolbar />
        <Box sx={{ overflow: 'auto' }}>
          <List>
            {menuItems.map((item) => (
              <ListItem
                button
                key={item.text}
                component={Link}
                to={item.link}
                selected={location.pathname === item.link}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItem>
            ))}
          </List>
          <Divider />
          <List>
            <ListItem button onClick={handleLogout}>
              <ListItemIcon><ExitToApp /></ListItemIcon>
              <ListItemText primary="Logout" />
            </ListItem>
          </List>
        </Box>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
};

export default AuthenticatedLayout;