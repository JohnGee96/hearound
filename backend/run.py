from server import app, initApiServer

def http(app):
    app.run(host='localhost', port=5000)

# def https(app):
#     app.run(host='0.0.0.0', port='443',)


if __name__ == '__main__':
    initApiServer('server.config.DevelopmentConfig')
    http(app)