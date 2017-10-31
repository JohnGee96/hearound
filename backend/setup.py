# List of commands need to setup the server 
# Used as a reference to build a setup script
# Assuming the following dependencies have been pre-installed:
# 1. sqlite3
# 2. Python 3.6

# virtualenv venv
# . venv/bin/activate
# pip install Flask==0.12.2 Flask-SQLAlchemy===2.1 Flask-Migrate==1.8.0 Flask-Restless==0.17.0
# pip freeze > requirements.txt

## Database initiation:
# mkdir db
# touch db/sqlite.db
# python manager.py db init
# python manager.py db migrate
# python manager.py db upgrade

## Running Server
# export FLASK_APP=app.py
# export FLASK_DEBUG=1
# flask run 