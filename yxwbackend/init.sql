-- ============================================================
-- 学生学术信息管理系统 - 数据库初始化脚本
-- 数据库名称: yxw
-- 使用方法：
--   1. 创建数据库: psql -U postgres -c "CREATE DATABASE yxw;"
--   2. 运行本脚本: psql -U postgres -d yxw -f init.sql
--   3. 启动后端，DataInitializer 会自动创建演示用户（密码均为 123456）
-- ============================================================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 学生信息表
CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    student_no VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(10),
    class_name VARCHAR(50),
    enrollment_year INTEGER
);

-- 教师表
CREATE TABLE IF NOT EXISTS teachers (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    teacher_no VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(50)
);

-- 课程表
CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    credits NUMERIC(3,1) NOT NULL
);

-- 课程-教师关联表
CREATE TABLE IF NOT EXISTS course_teachers (
    id SERIAL PRIMARY KEY,
    course_id INTEGER REFERENCES courses(id),
    teacher_id INTEGER REFERENCES teachers(id),
    class_name VARCHAR(50)
);

-- 成绩表（含工作流状态 + 补考字段）
CREATE TABLE IF NOT EXISTS grades (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES students(id),
    course_id INTEGER REFERENCES courses(id),
    score NUMERIC(5,2) NOT NULL,
    semester VARCHAR(20),
    academic_year VARCHAR(20),
    status VARCHAR(20) DEFAULT 'draft',
    is_retake BOOLEAN DEFAULT FALSE,
    original_score NUMERIC(5,2),
    retake_exam_score NUMERIC(5,2),
    entered_by INTEGER REFERENCES teachers(id),
    reviewed_by INTEGER REFERENCES teachers(id),
    UNIQUE(student_id, course_id, semester, academic_year)
);

-- 奖学金表
CREATE TABLE IF NOT EXISTS scholarships (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES students(id),
    type VARCHAR(50),
    amount NUMERIC(10,2),
    status VARCHAR(20) DEFAULT 'pending',
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
