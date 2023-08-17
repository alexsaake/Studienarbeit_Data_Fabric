const express = require('express');
const basicAuth = require('express-basic-auth');
const { createProxyMiddleware } = require('http-proxy-middleware');
const dev = process.env.NODE_ENV !== 'production';

const app = express();

const users = {
  // Replace 'username' with your desired username and 'password' with your desired password
  dataFabricUser: '123IHaveAccess',
};

app.use(
  basicAuth({
    users,
    challenge: true,
    unauthorizedResponse: 'Unauthorized',
  })
);

// Proxy middleware
app.use('/api', createProxyMiddleware({
  target: 'http://'+(dev ? 'localhost' : '192.168.178.11')+':8443',
  //target: 'http://localhost:8443', // Specify the target URL for proxying
  changeOrigin: false, // Add the 'host' header to the proxied request
}));

app.use(express.static('dist'));

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`\n\n\n\n--------------------------------------------------`);
  console.log(`Starting Data Fabric Prod server on port ${port}`);
});