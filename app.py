from flask import Flask, request, render_template, redirect
import json, time
from flask.ext.sqlalchemy import SQLAlchemy
from models import gsm, tracker,app

@app.route('/query', methods=['POST'])
def query():
  trackerid = request.form['id']
  people = gsm.query.filter_by(tracker=int(trackerid)).all()
  peoplelist = []
  for elem in people:
    peoplelist.append(elem.idtype + ": " +str(elem.id))
  return json.dumps(peoplelist)
    
@app.route('/')
def index():
    trackers = tracker.query.all()
    trackerlist = {}
    for elem in trackers:
	trackerlist[int(elem.trackernumber)] = elem.location.encode('ascii', 'ignore')
    print trackerlist
    return render_template('index.html',tracker = trackerlist)


if __name__ == "__main__":
  app.secret_key = 'A0Zr98j/3yX R~XHH!jmN]LWX/,?RT'
  app.run(host="0.0.0.0", port=8000,debug=True)
