// Node.js Backend: middleware/jwtVerify.js

const jwt = require('jsonwebtoken');

// This middleware verifies JWT tokens from ApexAuth
const jwtVerify = (req, res, next) => {
  try {
    // 1. Extract token from Authorization header
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ error: 'No token provided' });
    }
    
    const token = authHeader.substring(7); // Remove "Bearer "
    
    // 2. Verify token with same secret as ApexAuth
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    
    // 3. Extract user info from token
    req.user = {
      id: decoded.sub,           // User ID
      email: decoded.email,      // User email
      role: decoded.role         // User role
    };
    
    // 4. Continue to route handler
    next();
  } catch (error) {
    if (error.name === 'TokenExpiredError') {
      return res.status(401).json({ error: 'Token expired' });
    }
    return res.status(401).json({ error: 'Invalid token' });
  }
};

// Role-based authorization middleware
const requireRole = (role) => {
  return (req, res, next) => {
    if (req.user.role !== role) {
      return res.status(403).json({ error: 'Insufficient permissions' });
    }
    next();
  };
};

module.exports = { jwtVerify, requireRole };
