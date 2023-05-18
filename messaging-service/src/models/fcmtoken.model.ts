// import { FCMToken } from '@interfaces/fcmtoken.interface';
// import { DataTypes, Model, Optional, Sequelize } from 'sequelize';
// export type FCMCreationAttributes = Optional<FCMToken, 'id'>;

// export class FCMTokenModel extends Model<FCMToken, FCMCreationAttributes> implements FCMToken {
//   id: number;
//   token: string;
//   user: string;
//   isActive: boolean;
// }

// export default function (sequelize: Sequelize): typeof FCMTokenModel {
//   FCMTokenModel.init(
//     {
//       id: {
//         autoIncrement: true,
//         primaryKey: true,
//         type: DataTypes.INTEGER,
//       },
//       isActive: {
//         type: DataTypes.BOOLEAN,
//       },
//       token: {
//         type: DataTypes.STRING,
//       },
//       user: {
//         type: DataTypes.STRING,
//       },
//     },
//     {
//       tableName: 'fcm_token',
//       sequelize,
//     },
//   );
//   return FCMTokenModel;
// }
