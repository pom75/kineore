DATABASE SETUP - UBUNTU:

To connect the the database:
sudo mysql

To check users and auth methods:
SELECT user,authentication_string,plugin,host FROM mysql.user;


This commands alters the root user to be able to login with a password:
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
FLUSH PRIVILEGES;

Now to login we should use the command:
mysql -u root -p


Create user 'administrator' with password 'Kinecab5!'

CREATE USER 'administrator'@'localhost' IDENTIFIED BY 'Kinecab5!';
GRANT ALL PRIVILEGES ON *.* TO 'administrator'@'localhost' WITH GRANT OPTION;
