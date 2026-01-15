--
-- PostgreSQL database dump
-- Modified for LevelUp with Admin Support
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- 1. TABLE STRUCTURES
--

-- Table: achievements
CREATE TABLE public.achievements (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    description text,
    criteria_type character varying(50) NOT NULL,
    condition_value integer NOT NULL
);
ALTER TABLE public.achievements OWNER TO postgres;

CREATE SEQUENCE public.achievements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.achievements_id_seq OWNER TO postgres;
ALTER SEQUENCE public.achievements_id_seq OWNED BY public.achievements.id;


-- Table: study_programs
CREATE TABLE public.study_programs (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    created_at timestamp with time zone DEFAULT now()
);
ALTER TABLE public.study_programs OWNER TO postgres;

CREATE SEQUENCE public.study_programs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.study_programs_id_seq OWNER TO postgres;
ALTER SEQUENCE public.study_programs_id_seq OWNED BY public.study_programs.id;


-- Table: tasks
CREATE TABLE public.tasks (
    id bigint NOT NULL,
    title character varying(100) NOT NULL,
    description text,
    xp_reward integer DEFAULT 10 NOT NULL,
    category character varying(50),
    study_program_id bigint,
    is_active boolean DEFAULT true
);
ALTER TABLE public.tasks OWNER TO postgres;

CREATE SEQUENCE public.tasks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.tasks_id_seq OWNER TO postgres;
ALTER SEQUENCE public.tasks_id_seq OWNED BY public.tasks.id;


-- Table: user_achievements
CREATE TABLE public.user_achievements (
    user_id bigint NOT NULL,
    achievement_id bigint NOT NULL,
    unlocked_at timestamp with time zone DEFAULT now()
);
ALTER TABLE public.user_achievements OWNER TO postgres;


-- Table: user_tasks
CREATE TABLE public.user_tasks (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    task_id bigint NOT NULL,
    status character varying(20) DEFAULT 'PENDING'::character varying,
    assigned_date date DEFAULT CURRENT_DATE,
    completed_at timestamp with time zone,
    CONSTRAINT user_tasks_status_check CHECK (((status)::text = ANY (ARRAY['PENDING'::text, 'VERIFYING'::text, 'COMPLETED'::text, 'FAILED'::text])))
);
ALTER TABLE public.user_tasks OWNER TO postgres;

CREATE SEQUENCE public.user_tasks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.user_tasks_id_seq OWNER TO postgres;
ALTER SEQUENCE public.user_tasks_id_seq OWNED BY public.user_tasks.id;


-- Table: users (UPDATED WITH ROLE)
CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    password_hash character varying(255) NOT NULL,
    current_xp integer DEFAULT 0,
    current_level integer DEFAULT 1,
    streak integer DEFAULT 0,
    last_login_at timestamp with time zone,
    study_program_id bigint,
    role character varying(20) DEFAULT 'USER' NOT NULL, -- Added Role Column
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now(),
    CONSTRAINT users_current_xp_check CHECK ((current_xp >= 0))
);
ALTER TABLE public.users OWNER TO postgres;

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.users_id_seq OWNER TO postgres;
ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


-- Apply Defaults for ID columns
ALTER TABLE ONLY public.achievements ALTER COLUMN id SET DEFAULT nextval('public.achievements_id_seq'::regclass);
ALTER TABLE ONLY public.study_programs ALTER COLUMN id SET DEFAULT nextval('public.study_programs_id_seq'::regclass);
ALTER TABLE ONLY public.tasks ALTER COLUMN id SET DEFAULT nextval('public.tasks_id_seq'::regclass);
ALTER TABLE ONLY public.user_tasks ALTER COLUMN id SET DEFAULT nextval('public.user_tasks_id_seq'::regclass);
ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- 2. DATA POPULATION
--

-- Achievements
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (1, 'Just Getting Started', 'Complete your first task.', 'TASK_COUNT', 1);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (2, 'High Five', 'Complete 5 tasks.', 'TASK_COUNT', 5);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (3, 'Decathlete', 'Complete 10 tasks.', 'TASK_COUNT', 10);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (4, 'Quarter Century', 'Complete 25 tasks.', 'TASK_COUNT', 25);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (5, 'Task Master', 'Complete 50 tasks.', 'TASK_COUNT', 50);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (6, 'Centurion', 'Complete 100 tasks.', 'TASK_COUNT', 100);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (7, 'Productivity Machine', 'Complete 250 tasks.', 'TASK_COUNT', 250);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (8, 'Legendary Worker', 'Complete 500 tasks.', 'TASK_COUNT', 500);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (9, 'Task God', 'Complete 1,000 tasks.', 'TASK_COUNT', 1000);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (10, 'Experience Point', 'Earn your first 10 XP.', 'XP_TOTAL', 10);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (11, 'Rookie Earner', 'Reach 100 total XP.', 'XP_TOTAL', 100);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (12, 'Skill Collector', 'Reach 500 total XP.', 'XP_TOTAL', 500);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (13, 'Kilo-XP', 'Reach 1,000 total XP.', 'XP_TOTAL', 1000);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (14, 'Professional', 'Reach 2,500 total XP.', 'XP_TOTAL', 2500);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (15, 'Elite Status', 'Reach 5,000 total XP.', 'XP_TOTAL', 5000);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (16, 'Master of Arts', 'Reach 10,000 total XP.', 'XP_TOTAL', 10000);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (17, 'Grandmaster', 'Reach 25,000 total XP.', 'XP_TOTAL', 25000);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (18, 'Over 9000!', 'Reach 9,001 total XP.', 'XP_TOTAL', 9001);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (19, 'XP Millionaire', 'Reach 1,000,000 total XP.', 'XP_TOTAL', 1000000);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (20, 'Hat Trick', 'Log in for 3 days in a row.', 'STREAK_DAYS', 3);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (21, 'Week Warrior', 'Maintain a 7-day streak.', 'STREAK_DAYS', 7);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (22, 'Double Digits', 'Maintain a 10-day streak.', 'STREAK_DAYS', 10);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (23, 'Fortnight', 'Maintain a 14-day streak.', 'STREAK_DAYS', 14);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (24, 'Habit Former', 'Maintain a 21-day streak.', 'STREAK_DAYS', 21);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (25, 'Monthly Grind', 'Maintain a 30-day streak.', 'STREAK_DAYS', 30);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (26, 'Seasoned Regular', 'Maintain a 60-day streak.', 'STREAK_DAYS', 60);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (27, 'The 100 Club', 'Maintain a 100-day streak.', 'STREAK_DAYS', 100);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (28, 'Half Year Hero', 'Maintain a 180-day streak.', 'STREAK_DAYS', 180);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (29, 'Year of the Student', 'Maintain a 365-day streak.', 'STREAK_DAYS', 365);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (30, 'Level Up!', 'Reach Level 2.', 'LEVEL_THRESHOLD', 2);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (31, 'Taking Shape', 'Reach Level 5.', 'LEVEL_THRESHOLD', 5);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (32, 'Double Digits Level', 'Reach Level 10.', 'LEVEL_THRESHOLD', 10);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (33, 'Sweet Sixteen', 'Reach Level 16.', 'LEVEL_THRESHOLD', 16);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (34, 'Quarter Way', 'Reach Level 25.', 'LEVEL_THRESHOLD', 25);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (35, 'Mid-Game', 'Reach Level 50.', 'LEVEL_THRESHOLD', 50);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (36, 'High Roller', 'Reach Level 75.', 'LEVEL_THRESHOLD', 75);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (37, 'Max Level?', 'Reach Level 100.', 'LEVEL_THRESHOLD', 100);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (38, 'Fibonacci 5', 'Complete exactly 5 tasks (Fibonacci sequence).', 'TASK_COUNT', 5);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (39, 'Fibonacci 8', 'Complete exactly 8 tasks.', 'TASK_COUNT', 8);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (40, 'Fibonacci 13', 'Complete exactly 13 tasks.', 'TASK_COUNT', 13);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (41, 'Devil''s Number', 'Earn exactly 666 XP.', 'XP_TOTAL', 666);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (42, 'Lucky 777', 'Earn 777 XP.', 'XP_TOTAL', 777);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (43, 'Binary Solo', 'Complete 2 tasks (10 in binary).', 'TASK_COUNT', 2);
INSERT INTO public.achievements (id, name, description, criteria_type, condition_value) VALUES (44, 'Byte Sized', 'Complete 8 tasks.', 'TASK_COUNT', 8);


-- Study Programs
INSERT INTO public.study_programs (id, name, created_at) VALUES (1, 'Faculty of Arts and Design', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (2, 'Faculty of Chemistry, Biology, Geography', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (3, 'Faculty of Law', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (4, 'Faculty of Economics and Business Administration', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (5, 'Faculty of Physical Education and Sport', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (6, 'Faculty of Physics and Mathematics', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (7, 'Faculty of Computer Science', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (8, 'Faculty of Music and Theater', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (9, 'Faculty of Letters, History, Philosophy and Theology', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (10, 'Faculty of Sociology and Social Work', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (11, 'Faculty of Psychology and Educational Sciences', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (12, 'Faculty of Governance and Communication Sciences', now());
INSERT INTO public.study_programs (id, name, created_at) VALUES (13, 'Department for Teaching Staff Training', now());


-- Tasks
INSERT INTO public.tasks (title, description, xp_reward, category, study_program_id, is_active) VALUES 
('Quick Sketch Warmup', 'Grab a pencil and spend 15 minutes sketching the nearest object on your desk. Don''t worry about perfection, just capture the shape.', 15, 'Practical', 1, true),
('Color Hunt', 'Find three objects in your room that have complementary colors (e.g., Blue & Orange) and arrange them for a photo or quick study.', 20, 'Theory', 1, true),
('Renaissance Rebel', 'Read a short article about a Renaissance artist who broke the rules. Write down one technique they used.', 25, 'Reading', 1, true),
('Master the Pen Tool', 'Open Illustrator or Photoshop and practice tracing a complex shape using only the Pen Tool for 20 minutes.', 20, 'Skills', 1, true),
('Portfolio Polish', 'Pick one old piece of work and spend 30 minutes refining it. Fix that one detail that has been bothering you.', 50, 'Project', 1, true),
('Art Critic for a Day', 'Find a piece of art you dislike and write 3 sentences analyzing *why* it doesn''t work for you effectively.', 20, 'Analysis', 1, true),
('Font Detective', 'Look at a magazine or website and identify 3 different typefaces. Are they Serif or Sans-Serif?', 15, 'Typography', 1, true),
('Virtual Museum Trip', 'Visit the MoMA or Louvre website and find a painting that makes you feel something. Screenshot it for inspiration.', 15, 'Inspiration', 1, true),
('Perspective Challenge', 'Draw a simple street corner using 2-point perspective. Remember: all lines lead to the vanishing points!', 35, 'Practical', 1, true),
('Clean Your Gear', 'Wash your brushes, sharpen your pencils, or organize your digital layers. A clean workspace involves a clear mind.', 10, 'Maintenance', 1, true),
('Lab Safety Check', 'Visualize your next lab session. What are the three most critical safety hazards? Write them down.', 10, 'Safety', 2, true),
('Element Hunter', 'Pick a random element from the Periodic Table and find one real-world object that uses it.', 15, 'Chemistry', 2, true),
('Map Reader', 'Open Google Earth or a physical atlas. Find a location with high elevation and one below sea level.', 20, 'Geography', 2, true),
('Rockhound', 'Find a rock outside. Is it Igneous, Sedimentary, or Metamorphic? Use a guide to make your best guess.', 20, 'Geology', 2, true),
('Green Thumb', 'Identify a tree or plant on campus. Is it native to this region?', 15, 'Biology', 2, true),
('Equation Balancer', 'Take a complex chemical equation and balance it perfectly. Show your work!', 25, 'Chemistry', 2, true),
('Climate Detective', 'Look up the climate data for your city over the last 50 years. Do you see a trend?', 20, 'Geography', 2, true),
('GIS Wizard', 'Open your GIS software and successfully load a new dataset layer without errors.', 40, 'Skills', 2, true),
('Digestive Diagram', 'Sketch the human digestive system from memory, then check a textbook to see what you missed.', 20, 'Biology', 2, true),
('Nature Journal', 'Sit outside for 15 minutes without your phone. Write down observations about the weather, insects, or birds.', 25, 'Field Work', 2, true),
('Know Your Rights', 'Read the first 10 amendments (Bill of Rights) and pick the one you think is most debated today.', 15, 'Reading', 3, true),
('Case Breaker', 'Read a Supreme Court case summary. Identify the "Ratio Decidendi" (the reason for the decision).', 35, 'Analysis', 3, true),
('Latin Lesson', 'Learn the meaning of "Mens Rea" and "Actus Reus". Write a sentence using both.', 10, 'Vocabulary', 3, true),
('Opening Statement', 'Imagine you are defending a client accused of stealing a pie. Draft a 1-minute opening statement.', 40, 'Practical', 3, true),
('Contract Hawk', 'Read the Terms of Service for an app you use. Find one clause that seems unfair.', 30, 'Analysis', 3, true),
('Human Rights Watch', 'Read a recent report from a Human Rights organization. What is the main legal issue being raised?', 20, 'Reading', 3, true),
('Custody Battle', 'Review the factors judges use to determine child custody in your jurisdiction.', 20, 'Study', 3, true),
('Logic Games', 'Complete two LSAT-style logic puzzles. Don''t let the confusing wording trick you!', 20, 'Logic', 3, true),
('Courtroom Spy', 'Watch a recording of a real closing argument. Note how the lawyer uses emotion vs. logic.', 40, 'Field Work', 3, true),
('Hearsay Check', 'Explain the "Hearsay" rule to a friend or family member. If they understand it, you know it well.', 30, 'Study', 3, true),
('Supply & Demand', 'Draw a graph showing what happens to the price of coffee if a frost destroys the bean crop.', 20, 'Theory', 4, true),
('Market Watch', 'Check the S&P 500 or your local stock index. Did it go up or down today? Find one news article explaining why.', 15, 'Finance', 4, true),
('Balance the Books', 'Create a simple T-Account for a lemonade stand. Record $50 cash coming in and $20 expenses going out.', 30, 'Accounting', 4, true),
('Excel Ninja', 'Use VLOOKUP or XLOOKUP to merge data from two different columns in a spreadsheet.', 25, 'Skills', 4, true),
('The 4 Ps', 'Pick your favorite product. List its Product, Price, Place, and Promotion strategies.', 35, 'Marketing', 4, true),
('SWOT It Out', 'Perform a SWOT analysis (Strengths, Weaknesses, Opportunities, Threats) on Netflix.', 25, 'Strategy', 4, true),
('Case Study Dive', 'Read a business case study. What was the CEO''s biggest mistake?', 30, 'Reading', 4, true),
('Elasticity Test', 'Calculate the price elasticity of demand if price increases by 10% and demand drops by 20%.', 25, 'Theory', 4, true),
('Startup Spark', 'Write down 3 business ideas that solve a problem you faced today.', 20, 'Creativity', 4, true),
('Elevator Pitch', 'Record yourself giving a 60-second pitch for a product you invented. Sell it!', 25, 'Skills', 4, true),
('5k Challenge', 'Run or walk 5 kilometers. Track your time and try to beat it next week.', 40, 'Cardio', 5, true),
('Muscle Memory', 'Name the four muscles that make up the Quadriceps group without looking them up.', 20, 'Study', 5, true),
('Fuel Up', 'Plan a pre-workout meal that balances Carbs and Protein perfectly.', 30, 'Nutrition', 5, true),
('Dynamic Warmup', 'Lead a friend (or an imaginary class) through a 10-minute dynamic stretching routine.', 15, 'Practical', 5, true),
('Heavy Lifting', 'Focus on form: Perform 3 sets of a compound lift (Squat, Deadlift, or Bench) with perfect technique.', 35, 'Strength', 5, true),
('Rulebook Review', 'Pick a sport you don''t play often and read the official rules for fouls. You might be surprised.', 15, 'Theory', 5, true),
('Coach''s Corner', 'Design a drill to help a beginner improve their agility. Draw the cone placement.', 25, 'Coaching', 5, true),
('CPR Refresher', 'Watch a 5-minute refresher video on CPR and AED usage. It saves lives.', 20, 'Safety', 5, true),
('Game Tape', 'Watch 15 minutes of a professional match. Don''t watch the ball—watch the player movement off the ball.', 25, 'Analysis', 5, true),
('Hydration Station', 'Drink 500ml of water right after your workout. Your muscles need it!', 10, 'Health', 5, true),
('Calculus Crunch', 'Solve 5 derivatives. Try to do them without checking your formula sheet.', 30, 'Math', 6, true),
('Matrix Master', 'Calculate the determinant of a 3x3 matrix by hand. Show every step.', 25, 'Math', 6, true),
('Physics Lab Prep', 'Read the procedure for your next experiment twice. Visualize each step.', 15, 'Lab Work', 6, true),
('Quantum Weirdness', 'Read a short article about Wave-Particle Duality. Try to explain it to a 5-year-old.', 25, 'Theory', 6, true),
('Ray Tracer', 'Draw a ray diagram for a Convex Lens. Where does the image form?', 20, 'Physics', 6, true),
('Projectile Motion', 'Calculate how far a ball travels if thrown at 45 degrees at 20m/s (ignore air resistance).', 25, 'Physics', 6, true),
('Proof Writer', 'Write a formal proof for a basic geometry theorem. Make it elegant.', 40, 'Math', 6, true),
('Stats Check', 'Find a dataset and calculate the Standard Deviation. What does it tell you about the spread?', 30, 'Stats', 6, true),
('Code a Plot', 'Use Python (Matplotlib) or MATLAB to plot a sine wave function.', 30, 'Coding', 6, true),
('Circuit Builder', 'Draw a circuit with three resistors in parallel. Calculate the total resistance.', 20, 'Physics', 6, true),
('LeetCode Warmup', 'Solve one "Easy" problem on LeetCode or HackerRank. Focus on clean code.', 20, 'Coding', 7, true),
('Git Good', 'Make a meaningful commit to a project and push it to GitHub. Write a clear commit message.', 10, 'Version Control', 7, true),
('Bug Hunter', 'Find a bug in your old code (or a friend''s). Fix it and explain what went wrong.', 30, 'Debugging', 7, true),
('SQL Master', 'Write a query using a LEFT JOIN and a GROUP BY clause to filter a dataset.', 25, 'Database', 7, true),
('Rubber Ducking', 'Explain the concept of "Polymorphism" to a rubber duck (or an inanimate object).', 15, 'Theory', 7, true),
('Data Structs', 'Implement a Linked List from scratch without looking at a tutorial.', 35, 'Coding', 7, true),
('Flexbox Froggy', 'Spend 15 minutes practicing CSS Flexbox layouts. Center that div!', 30, 'Web Dev', 7, true),
('API Pinger', 'Use Postman or curl to make a GET request to a public API (like the PokeAPI).', 20, 'Testing', 7, true),
('Terminal Power', 'Perform 5 file operations (move, copy, delete) using only the command line.', 15, 'OS', 7, true),
('Clean Code', 'Refactor a messy function. Rename variables to be descriptive and break it into smaller functions.', 20, 'Coding', 7, true),
('Vocal Warmup', 'Do 15 minutes of scales and lip trills. Don''t skip the low notes!', 15, 'Warmup', 8, true),
('Scale Marathon', 'Play all 12 major scales at 100 BPM. Keep the rhythm steady.', 25, 'Practice', 8, true),
('Sight Reading', 'Pick a piece of music you''ve never seen and play 8 bars without stopping.', 20, 'Skills', 8, true),
('Line Memorization', 'Memorize 2 pages of dialogue. Try reciting it while doing chores to test retention.', 35, 'Acting', 8, true),
('Monologue Prep', 'Rehearse a dramatic monologue in front of a mirror. Focus on your facial expressions.', 25, 'Acting', 8, true),
('Chord Analysis', 'Take a pop song and analyze its chord progression using Roman Numerals (I, IV, V...).', 20, 'Theory', 8, true),
('Character Sketch', 'Draw or write a detailed description of the costume your character would wear.', 20, 'Design', 8, true),
('Improv Solo', 'Pick a random object in the room and improvise a 1-minute scene involving it.', 20, 'Acting', 8, true),
('Active Listening', 'Listen to a symphony or jazz piece with your eyes closed. Identify every instrument you hear.', 25, 'Listening', 8, true),
('Director''s Eye', 'Map out the blocking (movement) for a scene involving three characters.', 30, 'Directing', 8, true),
('Primary Source', 'Find a letter or diary entry from the era you are studying. Read it in its original phrasing.', 25, 'History', 9, true),
('Translation Challenge', 'Translate a short paragraph from a historical text. Use a dictionary if needed.', 25, 'Language', 9, true),
('Word Count Goal', 'Write 500 words of your term paper or thesis. Just get the ideas down.', 40, 'Writing', 9, true),
('Symbolism Hunter', 'Read a poem and highlight every symbol you find. What do they represent?', 25, 'Literature', 9, true),
('Timeline Builder', 'Draw a timeline of the 10 most important events of the 19th Century for your region.', 20, 'History', 9, true),
('Map History', 'Compare a map of Europe from 1914 to a map from 1920. List 3 major changes.', 20, 'History', 9, true),
('Logical Fallacy', 'Find a political speech and identify one logical fallacy (e.g., Ad Hominem).', 25, 'Philosophy', 9, true),
('Utilitarian Debate', 'Write down one argument FOR and one argument AGAINST Utilitarianism.', 30, 'Philosophy', 9, true),
('Linguistic Analysis', 'Listen to a recording of a dialect different from your own. Note the vowel shifts.', 25, 'Linguistics', 9, true),
('Archive Dig', 'Search a digital library (like JSTOR or Gutenberg) for a source published before 1950.', 20, 'Research', 9, true),
('People Watching', 'Spend 20 minutes in a public space (cafe/park) observing social interactions. Take notes.', 20, 'Field Work', 10, true),
('Survey Design', 'Draft 5 unbiased questions for a hypothetical research study on social media usage.', 25, 'Research', 10, true),
('Data Crunch', 'Open a dataset in SPSS or Excel and calculate the mean and median of a variable.', 35, 'Stats', 10, true),
('Theory Recap', 'Summarize "Alienation" by Marx or "Anomie" by Durkheim in your own words.', 25, 'Theory', 10, true),
('Case Note', 'Write a mock case note for a client scenario. Focus on objective observation.', 25, 'Social Work', 10, true),
('Active Listening', 'Practice active listening in your next conversation. Summarize what they said back to them.', 20, 'Skills', 10, true),
('Genogram', 'Draw a family tree (genogram) for a fictional character. Map out the relationships.', 30, 'Social Work', 10, true),
('Policy Critique', 'Read a summary of a recent social policy. Who benefits? Who is left out?', 30, 'Policy', 10, true),
('Community Map', 'Identify 3 resources (clinics, NGOs, parks) in your local neighborhood map.', 25, 'Research', 10, true),
('Ethics Check', 'Review the Code of Ethics for Social Workers. Pick one principle to focus on today.', 20, 'Ethics', 10, true),
('Lesson Planner', 'Draft a 45-minute lesson plan on a topic of your choice. Include a "Hook" and a "Closing".', 35, 'Education', 11, true),
('Piaget Review', 'List the 4 stages of cognitive development. Which stage is a 7-year-old in?', 20, 'Psychology', 11, true),
('Memory Test', 'Design a quick memory experiment and test it on a friend. Record the results.', 25, 'Practical', 11, true),
('Brain Map', 'Draw a side view of the brain and label the four lobes. Color code them!', 20, 'Biology', 11, true),
('Chaos Control', 'List 3 strategies you would use to handle a disruptive student in class.', 25, 'Education', 11, true),
('Rubric Maker', 'Create a grading rubric for an oral presentation. What defines an "A" vs a "B"?', 25, 'Education', 11, true),
('IEP Review', 'Read about Individualized Education Programs. What is the most important legal requirement?', 25, 'Education', 11, true),
('Behaviorism vs. Constructivism', 'Write one paragraph comparing these two learning theories.', 25, 'Theory', 11, true),
('Psychoanalysis', 'Read a summary of Freud''s Id, Ego, and Superego. Can you apply it to a movie character?', 20, 'Psychology', 11, true),
('Correlation Check', 'Look at a scatter plot. Is the correlation positive, negative, or zero?', 30, 'Stats', 11, true),
('Headline Hunter', 'Compare how two different news outlets cover the exact same event. Note the bias.', 20, 'Analysis', 12, true),
('Press Release', 'Draft a 1-page press release for a fictional event. Keep it punchy and professional.', 30, 'Writing', 12, true),
('Impromptu Speech', 'Record yourself speaking for 2 minutes on a random topic without preparation.', 25, 'Skills', 12, true),
('Debate Prep', 'Outline 3 strong arguments for a policy you actually disagree with.', 30, 'Logic', 12, true),
('Machiavelli Time', 'Read one chapter of "The Prince". Is it relevant to modern politics?', 20, 'Reading', 12, true),
('Campaign Strategy', 'Sketch a social media plan for a candidate running for local office.', 35, 'Strategy', 12, true),
('Crisis Comms', 'Imagine a PR disaster. Write the first tweet your organization would send to handle it.', 30, 'PR', 12, true),
('UN Watch', 'Read a summary of a recent UN Security Council resolution.', 20, 'Reading', 12, true),
('Separation of Powers', 'Draw a diagram showing how the Executive, Legislative, and Judicial branches interact.', 15, 'Gov', 12, true),
('Policy Brief', 'Write a one-page brief recommending a solution to a local traffic problem.', 35, 'Writing', 12, true),
('Bloom''s Taxonomy', 'Identify a learning objective for each level of Bloom''s Taxonomy for your subject.', 20, 'Theory', 13, true),
('Tech Teacher', 'Create a Kahoot or Quizlet set for a topic you plan to teach.', 20, 'Tech', 13, true),
('Formative Assessment', 'Design a quick "Exit Ticket" activity to check student understanding at the end of class.', 25, 'Planning', 13, true),
('Think-Pair-Share', 'Script out how you would explain the instructions for a Think-Pair-Share activity.', 20, 'Methods', 13, true),
('Inclusive Classroom', 'List 3 modifications you could make for a student with visual impairments.', 25, 'Diversity', 13, true),
('Anxiety Awareness', 'Read an article about how test anxiety affects student performance.', 20, 'Psychology', 13, true),
('Teaching Artifact', 'Create a handout or worksheet that you could add to your teaching portfolio.', 30, 'Career', 13, true),
('Micro-Lecture', 'Practice delivering a 5-minute explanation of a concept to an empty room.', 30, 'Practical', 13, true),
('Mentor Questions', 'Write down 3 questions you would want to ask an experienced teacher-mentor.', 15, 'Career', 13, true),
('Legal Eagle', 'Review the laws regarding "Duty of Care" for teachers in your country.', 20, 'Law', 13, true),
('Hydration Hero', 'Drink 2 liters of water today. Your brain needs it to focus!', 10, 'Health', NULL, true),
('Morning Mobility', 'Do a 5-minute stretch right after waking up. Touch those toes!', 10, 'Health', NULL, true),
('Walk it Off', 'Take a 15-minute brisk walk outside. Fresh air fixes coding bugs.', 20, 'Health', NULL, true),
('Step Master', 'Hit 10,000 steps today. Use the stairs instead of the elevator.', 40, 'Health', NULL, true),
('Nature''s Candy', 'Replace a sugary snack with an apple, banana, or orange.', 10, 'Nutrition', NULL, true),
('Caffeine Cut', 'Skip the energy drink or soda today. Stick to water, tea, or black coffee.', 15, 'Nutrition', NULL, true),
('Sleep Tight', 'Commit to getting at least 7.5 hours of sleep tonight. Set an alarm for bedtime.', 30, 'Health', NULL, true),
('Headspace', 'Spend 10 minutes meditating or just sitting in silence. No phone allowed.', 20, 'Mental Health', NULL, true),
('Digital Detox', 'Put your phone in another room for 1 hour before you go to sleep.', 25, 'Mental Health', NULL, true),
('Quick Burn', 'Do 20 pushups or squats right now. Get the blood flowing!', 35, 'Fitness', NULL, true),
('Gym Rat', 'Go to the gym for at least 45 minutes. Lift heavy or run hard.', 50, 'Fitness', NULL, true),
('Vitamin Boost', 'Don''t forget to take your daily vitamins or eat some veggies.', 5, 'Health', NULL, true),
('Floss Like a Boss', 'Floss your teeth tonight. Your dentist knows if you lie.', 10, 'Health', NULL, true),
('Posture Check', 'Stand up and stretch your back every hour while studying.', 15, 'Health', NULL, true),
('Chef Mode', 'Cook a meal from scratch instead of ordering delivery. It tastes better!', 40, 'Nutrition', NULL, true),
('Gratitude Journal', 'Write down 3 things you are grateful for today. It rewires your brain for positivity.', 15, 'Mental Health', NULL, true),
('Screen-Free Meal', 'Eat lunch without watching YouTube or scrolling TikTok. Taste your food.', 15, 'Mental Health', NULL, true),
('Make Your Bed', 'Start the day with a small win. It takes 2 minutes.', 5, 'Chores', NULL, true),
('Dish Destroyer', 'Clean every single dish in the sink. No "soaking" allowed!', 20, 'Chores', NULL, true),
('Laundry Day', 'Wash, dry, AND fold a load of laundry. The folding is the hard part.', 35, 'Chores', NULL, true),
('Trash Trek', 'Empty all the trash bins in your room or apartment.', 10, 'Chores', NULL, true),
('Floor Patrol', 'Vacuum or sweep your room. A clean floor feels amazing.', 25, 'Chores', NULL, true),
('Bathroom Blitz', 'Scrub the sink and mirror. You deserve a clean bathroom.', 40, 'Chores', NULL, true),
('Fresh Sheets', 'Change your bedsheets. Best feeling in the world.', 25, 'Chores', NULL, true),
('Plant Parent', 'Check your plants. Do they need water? Sunlight? A pep talk?', 10, 'Chores', NULL, true),
('Desk Declutter', 'Clear off your desk. Throw away old receipts and organize your pens.', 15, 'Chores', NULL, true),
('Fridge Raid', 'Check the fridge for expired food. Throw it out before it becomes a science experiment.', 30, 'Chores', NULL, true),
('Closet Chaos', 'Spend 15 minutes organizing your clothes or shoes.', 25, 'Chores', NULL, true),
('Tech Wipe', 'Clean your phone screen and keyboard. They are dirtier than you think.', 10, 'Chores', NULL, true),
('Grocery Run', 'Go buy actual groceries so you don''t starve this week.', 35, 'Chores', NULL, true),
('Meal Prepper', 'Prep your lunches for the next 3 days. Future You will thank you.', 50, 'Chores', NULL, true),
('Window Wipe', 'Clean the windows in your room to let more light in.', 20, 'Chores', NULL, true),
('Dust Buster', 'Dust off your bookshelves and monitor.', 15, 'Chores', NULL, true),
('Reader', 'Read 10 pages of a non-fiction book. Learn something new.', 20, 'Personal', NULL, true),
('Bookworm', 'Read for pleasure for 30 minutes. Fiction counts!', 30, 'Personal', NULL, true),
('Podcast Pal', 'Listen to an educational podcast while you commute or clean.', 20, 'Personal', NULL, true),
('Budget Boss', 'Update your expense tracker. Know where your money went.', 15, 'Finance', NULL, true),
('Piggy Bank', 'Transfer $5 (or more) to your savings account right now.', 10, 'Finance', NULL, true),
('Sub Scrub', 'Check your bank statement for subscriptions you don''t use anymore. Cancel one.', 20, 'Finance', NULL, true),
('Plan Tomorrow', 'Write down the 3 most important things you need to do tomorrow.', 15, 'Productivity', NULL, true),
('Inbox Zero', ' spend 15 minutes archiving or deleting old emails.', 25, 'Productivity', NULL, true),
('LinkedIn Polish', 'Update your CV or LinkedIn profile. Add that new skill you learned.', 30, 'Career', NULL, true),
('Call Home', 'Call a parent or sibling just to say hi. Texting doesn''t count.', 20, 'Social', NULL, true),
('IRL Hangout', 'Spend time with a friend in person. Put the phone away.', 25, 'Social', NULL, true),
('Hobby Time', 'Spend 30 minutes on a hobby that isn''t school/work (guitar, drawing, knitting).', 30, 'Personal', NULL, true),
('Word of the Day', 'Learn a fancy new word and try to use it in a conversation.', 5, 'Learning', NULL, true),
('Docu-Night', 'Watch a documentary instead of a reality show.', 25, 'Learning', NULL, true),
('Data Backup', 'Back up your important files to the cloud or an external drive.', 20, 'Productivity', NULL, true),
('Unsubscribe', 'Unsubscribe from 5 marketing emails you never open.', 10, 'Productivity', NULL, true),
('Backpack Dump', 'Clean out your bag. You don''t need those old gum wrappers.', 10, 'Organization', NULL, true),
('Syllabus Check', 'Look at your calendar. When is your next big deadline?', 15, 'Study', NULL, true),
('Charge Up', 'Make sure your laptop and phone are plugged in before bed.', 5, 'Preparation', NULL, true),
('Note Review', 'Spend 15 minutes reviewing notes from today''s classes while they are fresh.', 20, 'Study', NULL, true),
('Desktop Cleanse', 'Organize your computer desktop. Delete the "Untitled_1.docx" files.', 15, 'Organization', NULL, true);


--
-- 3. CONSTRAINTS & INDICES
--

-- Primary Keys
ALTER TABLE ONLY public.achievements ADD CONSTRAINT achievements_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.study_programs ADD CONSTRAINT study_programs_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.tasks ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.users ADD CONSTRAINT users_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.user_tasks ADD CONSTRAINT user_tasks_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.user_achievements ADD CONSTRAINT user_achievements_pkey PRIMARY KEY (user_id, achievement_id);

-- Unique Constraints
ALTER TABLE ONLY public.study_programs ADD CONSTRAINT study_programs_name_key UNIQUE (name);
ALTER TABLE ONLY public.users ADD CONSTRAINT users_email_key UNIQUE (email);
ALTER TABLE ONLY public.users ADD CONSTRAINT users_username_key UNIQUE (username);

-- Foreign Keys
ALTER TABLE ONLY public.tasks ADD CONSTRAINT tasks_study_program_id_fkey FOREIGN KEY (study_program_id) REFERENCES public.study_programs(id);
ALTER TABLE ONLY public.users ADD CONSTRAINT users_study_program_id_fkey FOREIGN KEY (study_program_id) REFERENCES public.study_programs(id);
ALTER TABLE ONLY public.user_tasks ADD CONSTRAINT user_tasks_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.tasks(id);
ALTER TABLE ONLY public.user_tasks ADD CONSTRAINT user_tasks_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
ALTER TABLE ONLY public.user_achievements ADD CONSTRAINT user_achievements_achievement_id_fkey FOREIGN KEY (achievement_id) REFERENCES public.achievements(id) ON DELETE CASCADE;
ALTER TABLE ONLY public.user_achievements ADD CONSTRAINT user_achievements_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- 4. SEQUENCE UPDATES
--
SELECT pg_catalog.setval('public.achievements_id_seq', 44, true);
SELECT pg_catalog.setval('public.study_programs_id_seq', 13, true);
SELECT pg_catalog.setval('public.user_tasks_id_seq', 1, false);