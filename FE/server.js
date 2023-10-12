const express = require('express');
const basicAuth = require('express-basic-auth');
const { createProxyMiddleware } = require('http-proxy-middleware');
const dev = process.env.NODE_ENV !== 'production';

const app = express();

const users = {
  dataFabricUser: '123IHaveAccess',
};

// Forwarding middleware
app.use('/api', createProxyMiddleware({
  target: 'http://'+(true ? 'localhost' : '192.168.178.11')+':8443',
  changeOrigin: false, // Add the 'host' header to the proxied request
}));

// Basic auth middleware
app.use(
  basicAuth({
    users,
    challenge: true,
    unauthorizedResponse: 'Unauthorized'
    })
);

app.use(express.static('dist'));

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`\n\n\n\n--------------------------------------------------`);
  console.log(`Starting Data Fabric Prod server on port ${port}`);
});