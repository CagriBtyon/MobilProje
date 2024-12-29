import mongoose from "mongoose";

const TaskSchema = new mongoose.Schema({
    username: {type: String, required: true},
    taskname: {type: String, required: true},
    explanation: {type: String, required: false},
    completed: {type: Boolean, required: false},   
    deadline: { type: Date, required: false }, // Son teslim tarihi için yeni alan 
})

export const TaskModel = mongoose.model('Task' , TaskSchema);

export const getTasks = () => TaskModel.find();
export const getTaskByUserName = (username: string) => TaskModel.findOne({username});
export const getTaskById = (id: string) => TaskModel.findById(id);
export const getTaskByName = (taskname: string) => TaskModel.findOne({taskname});
export const createTask = (values: Record<string, any>) => new TaskModel(values)
    .save().then((task) => task.toObject());
export const deleteTaskById = (id: string) => TaskModel.findOneAndDelete({_id: id});
export const updateTaskById = (id: string, values: Record<string,any>) => TaskModel.findByIdAndUpdate(id, values);

