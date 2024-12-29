import express from 'express'
import { authentication, random } from '../helpers';
import { createUser, getUserByEmail } from '../db/users';

export const register: express.RequestHandler = async (req: express.Request, res: express.Response) => {
    try {
        console.log(req);
        const { email, password, username } = req.body;
        if (!email || !password || !username) {
            res.sendStatus(400);
            return;
        }

        const existingUser = await getUserByEmail(email);
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

        res.status(200).json(user);
    } catch (error) {
        console.error(error);
        res.sendStatus(400);
    }
};

