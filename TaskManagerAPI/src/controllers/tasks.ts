import { getUserByUserName } from '../db/users';
import { createTask, deleteTaskById, getTaskById, getTaskByName, getTasks, updateTaskById } from '../db/tasks';
import express from 'express'

export const getAllTasks: express.RequestHandler = async (req: express.Request , res: express.Response) => {
    try{
        const tasks = await getTasks();

        res.status(200).json(tasks);
    }
    catch(error){
        console.log(error);
        res.sendStatus(400);
    }
}

export const getTaskByID: express.RequestHandler = async (req: express.Request , res: express.Response) => {
    try{
        const {id} = req.body;
        if(!id){
            res.sendStatus(400);
        }
        const task = await getTaskById(id);
        res.status(200).json(task);
    }
    catch(error){
        console.log(error);
        res.sendStatus(400);
    }
}

export const getTaskByTaskName: express.RequestHandler = async (req: express.Request , res: express.Response) => {
    try{
        const {taskname} = req.body;
        if(!taskname){
            res.sendStatus(400);
        }
        const task = await getTaskByName(taskname);
        res.status(200).json(task);
    }
    catch(error){
        console.log(error);
        res.sendStatus(400);
    }
}

export const updateTaskByID: express.RequestHandler = async (req: express.Request , res: express.Response) => {
    try{
        const { id } = req.params;
        const {username , taskname , explanation , completed , deadline} = req.body;
        if(!id){
            res.sendStatus(400);
        }
        if(!username || !taskname){
            res.sendStatus(400);
        }
        const task = await updateTaskById(id , {
            username,
            taskname,
            explanation,
            completed,
            deadline
        })
        res.status(200).json(task).end()
    }
    catch(error){
        console.log(error);
        res.sendStatus(400);
    }
}

export const deleteTaskByID: express.RequestHandler = async (req: express.Request , res: express.Response) => {
    try{
        const {id} = req.body;
        if(!id){
            res.sendStatus(400);            
        }

        const task = await deleteTaskById(id);
        
        res.status(200).json(task);
    }
    catch(error){
        console.log(error);
        res.sendStatus(400);
    }
}

export const createNewTask: express.RequestHandler = async (req: express.Request , res: express.Response) => {
    try{
        const {username , taskname , explanation , completed , deadline} = req.body;
        if(!username || !taskname){
            res.sendStatus(400);
        }
        const existingTask = await getTaskByName(taskname);
        if(existingTask){
            res.sendStatus(400);
        }

        const existingUser = await getUserByUserName(username);        
        if(!existingUser){
            res.sendStatus(400);
        }

        const task = await createTask({
            username,
            taskname,
            explanation,
            completed,
            deadline
        });

        res.status(200).json(task).end();
    }
    catch(error){
        console.log(error);
        res.sendStatus(400);
    }
}
