import express from 'express'
import { authentication, random } from '../helpers';
import { createUser, getUserByUserName } from '../db/users';

export const login: express.RequestHandler = async (req: express.Request , res: express.Response) => {
    try {
        const { username , password } = req.body;
        if(!username || !password){
            res.sendStatus(400);
        }
        
        const user = await getUserByUserName(username).select('+authentication.salt +authentication.password');
        if(!user){
            res.sendStatus(400);
        }

        const expectedHash = authentication(user.authentication.salt , password);
        if(user.authentication.password !== expectedHash){
            res.sendStatus(403);
        }

        const salt = random();
        user.authentication.sessionToken = authentication(salt , user._id.toString());
        await user.save();

        res.cookie("AUTH" , user.authentication.sessionToken , {domain: "localhost" , 
            path: "/"
        });
        res.status(200).json(user).end();
        
    } catch(error){
        console.log(error);
        res.sendStatus(400);
    }
}

export const register: express.RequestHandler = async (req: express.Request, res: express.Response) => {
    try {
        console.log(req);
        const { email, password, username } = req.body;
        if (!email || !password || !username) {
            res.sendStatus(400);
            return;
        }

        const existingUser = await getUserByUserName(email);
        if (existingUser) {
            res.sendStatus(400);
            return;
        }

        const salt = random();
        const user = await createUser({
            email,
            username,
            authentication: {
                salt,
                password: authentication(salt, password),
            },
        });

        res.status(200).json(user).end();
    } catch (error) {
        console.error(error);
        res.sendStatus(400);
    }
};

