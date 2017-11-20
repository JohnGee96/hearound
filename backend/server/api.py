# Define API endpoints using Flask-Restless
from server.models import Post, User

def createApi(manager):
    user_cols = ['id', 'username', 'email', 'post', 'post.id']
    manager.create_api(User, methods=['Get'], include_columns=user_cols)
    post_cols = ['id','user','user.username', 'user.id','body','lat','lng']
    manager.create_api(Post, methods=['GET','POST', 'PUT'], include_columns=post_cols)