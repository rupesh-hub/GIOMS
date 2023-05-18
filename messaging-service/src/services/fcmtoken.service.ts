// import DB from "@databases"
// import {FCMTokenModel} from "@models/fcmtoken.model";

// export class FCMTokenService{
//   public FCMToken = DB.FCMToken;
// constructor() {
// }
// async saveToken(token,username):Promise<FCMTokenModel>{
//   const allTokens:Array<FCMTokenModel> = await this.FCMToken.findAll({
//     where:{
//       user:username
//     }
//   });
//   allTokens.forEach(data=>{
//     data.isActive = false;
//     data.save();
//   })
//     const existingTokens:Array<FCMTokenModel> = await this.FCMToken.findAll({
//       where:{
//         user:username,
//         token:token
//       }
//     });
//     if(existingTokens.length===0){
//       return this.FCMToken.create({
//         user:username,
//         token:token,
//         isActive:true
//       });
//     }
//     else {
//       existingTokens.forEach(data=>{
//         data.isActive = true;
//         data.save();
//       })
//       return existingTokens[0];
//     }
// }
// }
