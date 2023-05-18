import { NextFunction, Request, Response } from 'express';
import { KeycloakService } from '../keycloak';
const keycloakService = new KeycloakService();
const authMiddleWare = app => {
  return app.use(async (req, res: Response, next: NextFunction) => {
    try {
      const keycloak = await keycloakService.getKeycloakInstance();
      console.log(req.path);
      if (req.path.includes('/docs')) {
        next();
      } else {
        console.log('hit');
        return keycloak.protect()(req, res, () => {
          console.log('hit');
          // @ts-ignore
          const userInfo = (({ sub, email_verified, preferred_username, email, username }) => ({
            sub,
            email_verified,
            preferred_username,
            email,
            username,
          }))(req.kauth.grant.access_token.content);
          console.log(userInfo);
          // const sub: string = userInfo.sub;
          // const data: string[] = sub.split(':');
          userInfo['id'] = userInfo.username.toUpperCase();
          req['userInfo'] = userInfo;
          next();
        });
      }
    } catch (ex) {
      next(ex);
    }
  });
};

export default authMiddleWare;
