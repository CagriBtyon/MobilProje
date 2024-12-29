import { createNewTask, deleteTaskByID, getAllTasks, getTaskByID, getTaskByTaskName, updateTaskByID } from '../controllers/tasks';
import express from 'express';

export default (router: express.Router) => {
    router.get("/getAllTasks" , getAllTasks);
    router.post("/createTask" , createNewTask);
    router.get("/getTaskById", getTaskByID);
    router.get("/getTaskByTaskName", getTaskByTaskName);
    router.put("/updateTaskByID/:id", updateTaskByID);
    router.delete("/deleteTaskByID" , deleteTaskByID)
} 