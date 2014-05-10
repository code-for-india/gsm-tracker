from flask import Flask, request, render_template, redirect
import json, time
from flask.ext.sqlalchemy import SQLAlchemy
from models import gsm, tracker,app


@app.route('/')
def index():
    return render_template('index.html')


if __name__ == "__main__":
  app.secret_key = 'A0Zr98j/3yX R~XHH!jmN]LWX/,?RT'
  app.run(host="0.0.0.0", port=8000,debug=True)
