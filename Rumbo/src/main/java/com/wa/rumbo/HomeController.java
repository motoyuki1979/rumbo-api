package com.wa.rumbo;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import models.AddCategoryCommentModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import models.AddPost;
import models.BaseResponse;
import models.Categories;
import models.GetCalBooking;
import models.GetComments;
import models.GetComunityComments;
import models.GetFollowers;
import models.GetNotifications;
import models.GetPost;
import models.PostComment;
import models.PostDetailModel;
import models.User;
import models.UserDetailModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
  
/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

    @Autowired
    DbServiceImpl dbServiceImpl;
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        logger.info("Welcome home! The client locale is {}.", locale);

        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

        String formattedDate = dateFormat.format(date);

        model.addAttribute("serverTime", formattedDate);

        return "home";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> registerUser(@RequestParam("device_id") String device_id,
            @RequestParam("device_token") String device_token,@RequestParam("email") String email,
            @RequestParam("password") String password, @RequestParam("following_count") String following_count, @RequestParam("follower_count") String follower_count) {
        BaseResponse baseResponse = new BaseResponse();
        if (email != null) {
            User user = dbServiceImpl.checkUser(email);
            if (user != null) {
                baseResponse.setObject(user);
                baseResponse.setSuccess("false");
                baseResponse.setMessage("User already exists");
                ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                        HttpStatus.OK);
                return responseEntity;
            } else {
                int res = dbServiceImpl.insertUser(device_id, device_token, email, password, following_count, follower_count);
                if (res != 0) {
                    User user2 = dbServiceImpl.checkUser(email);
                    baseResponse.setObject(user2);
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Registered Successfully");
                    ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                            HttpStatus.OK);
                    return responseEntity;
                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Registered Failed !");
                    ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                            HttpStatus.BAD_REQUEST);
                    return responseEntity;
                }
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("Something went wrong");
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                    HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }
    
     @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> loginUser(@RequestParam("device_id") String device_id,
            @RequestParam("device_token") String device_token,@RequestParam("email") String email,
            @RequestParam("password") String password) {
        BaseResponse baseResponse = new BaseResponse();
        if (email != null) {
            User user = dbServiceImpl.checkUser(email);
            if (user != null) {
                if(password.equals(user.getPassword())){
                    baseResponse.setObject(user);
                baseResponse.setSuccess("true");
                baseResponse.setMessage("Login Successfully");
                ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                        HttpStatus.OK);
                  return responseEntity;
                }else{
                   baseResponse.setSuccess("false");
                    baseResponse.setMessage("Incorrect password");
                    ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                            HttpStatus.BAD_REQUEST); 
                      return responseEntity;
                }
              
            } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Invalid email or password!");
                    ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                            HttpStatus.BAD_REQUEST);
                    return responseEntity;
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("Something went wrong");
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                    HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }
 
    @RequestMapping(value = "/category_list", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> categoryList() {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
     //  boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        //if (headerValid) {
            List<Categories> categories = dbServiceImpl.getCategories();
            baseResponse.setObject(categories);
            baseResponse.setSuccess("true");
            baseResponse.setMessage("Data fetch sucessfully");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//        } else {
//            baseResponse.setSuccess("false");
//            baseResponse.setMessage("invalid request");
//            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
//        }
        return responseEntity;
    }

    @RequestMapping(value = "/add_post", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> addPost(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestBody AddPost addPost) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            if (addPost != null) {
                int res = dbServiceImpl.addPost(addPost, user_id);
                if (res > 0) {
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Post add successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameters !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    @RequestMapping(value = "/get_all_post", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getAllPost(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
       // if (headerValid) {
            List<GetPost> listPosts = dbServiceImpl.getPosts(user_id);
            if (listPosts.size() > 0) {
                baseResponse.setObject(listPosts);
                baseResponse.setSuccess("true");
                baseResponse.setMessage("Post fetch successfully");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//            } else {
//                baseResponse.setSuccess("false");
//                baseResponse.setMessage("Failed !");
//                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
//            }

        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    @RequestMapping(value = "/post_comment", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> postComment(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestBody PostComment postComment) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
       // if (headerValid) {
            if (postComment != null) {
                int res = dbServiceImpl.postComment(postComment, user_id);
                if (res > 0) {
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//                } else {
//                    baseResponse.setSuccess("false");
//                    baseResponse.setMessage("Failed !");
//                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
//                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameters !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    @RequestMapping(value = "/get_post_comment", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getPostComment(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("post_id") String post_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        //if (headerValid) {
            if (post_id != null) {
                List<GetComments> list = dbServiceImpl.getPostComments(post_id, user_id);
                if (list.size() > 0) {
                    baseResponse.setObject(list);
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment fetch successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//                } else {
//                    baseResponse.setObject(list);
//                    baseResponse.setSuccess("true");
//                    baseResponse.setMessage("Comment not found");
//                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 400
//                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameter !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }

        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    @RequestMapping(value = "/get_post_detail", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<PostDetailModel> getPostDetails(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("post_id") String post_id) {
        ResponseEntity<PostDetailModel> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        PostDetailModel detailsModel = new PostDetailModel();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
       // if (headerValid) {
            if (post_id != null) {
                List<GetComments> list = dbServiceImpl.getPostComments(post_id, user_id);
                GetPost postDetail = dbServiceImpl.getPostDetails(post_id, user_id);

//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("post",postDetail);
//            jsonObject.put("post_comments",list);
                detailsModel.setPost_comments(list);
                detailsModel.setPost(postDetail);
                detailsModel.setSuccess("true");
                detailsModel.setMessage("fetch successfully");
                responseEntity = new ResponseEntity<PostDetailModel>(detailsModel, HttpStatus.OK);// 200
          //            } else {
//                detailsModel.setSuccess("false");
//                detailsModel.setMessage("Null Parameter !");
//                responseEntity = new ResponseEntity<PostDetailModel>(detailsModel, HttpStatus.BAD_REQUEST);// 400
//            }
        } else {
            detailsModel.setSuccess("false");
            detailsModel.setMessage("invalid request");
            responseEntity = new ResponseEntity<PostDetailModel>(detailsModel, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    @RequestMapping(value = "/post_like", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> postLike(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("post_id") String post_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            if (post_id.trim().length() > 0) {
                int res = dbServiceImpl.likePost(post_id, user_id);
                if (res > 0) {
                    if (res == 1) {
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Like successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                    } else {
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("UnLike successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                    }

                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameters !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    @RequestMapping(value = "/comment_like", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> commentLike(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("comment_id") String comment_id,
            @RequestParam("post_id") String post_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            if (comment_id.trim().length() > 0) {
                int res = dbServiceImpl.likeComment(comment_id, user_id, post_id);
                if (res > 0) {
                    if (res == 1) {

                    //    String message = dbServiceImpl.insertNotificationData(comment_id, user_id, post_id);
                      //  String device_token = dbServiceImpl.getDeviceTokenByPostID(post_id);
//
//                        JSONObject details = new JSONObject();
//                        details.put("comment_id", comment_id);
//                        details.put("post_id", post_id);
                        //sendAndroidNotification(details, device_token, message);

                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Comment Like successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200

                    } else {
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Comment UnLike successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                    }

                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameters !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    private ResponseEntity<String> sendAndroidNotification(JSONObject details, String token, String message) {
        // TODO Auto-generated method stub
        if (!token.isEmpty()) {
            try {
                JSONObject body = new JSONObject();
                body.put("to", token);
                body.put("priority", "high");

                JSONObject notification = new JSONObject();
                notification.put("title", "PetCare");
                notification.put("body", message);
                notification.put("icon", "logo.png");

                JSONObject data = new JSONObject();
                data.put("title", "PetCare");
                data.put("body", message);
                data.put("icon", "logo.png");
                data.put("details", details);

                body.put("notification", notification);
                body.put("data", data);
                //body.put("registration_id", token);

                System.out.println("body==" + body.toString());
                HttpEntity<String> request = new HttpEntity<String>(body.toString());

                CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
                CompletableFuture.allOf(pushNotification).join();

                String firebaseResponse = pushNotification.get();
                //logger.debug("firebaseResponse=="+firebaseResponse);
                System.out.println("firebaseResponse==" + firebaseResponse);
                return new ResponseEntity<String>(firebaseResponse, HttpStatus.OK);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return new ResponseEntity<String>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/notifications", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getNotificationList(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {

            List<GetNotifications> notificationList = dbServiceImpl.getNotificationList(user_id);
            baseResponse.setSuccess("true");
            baseResponse.setMessage("");
            baseResponse.setObject(notificationList);
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    @RequestMapping(value = "/get_user_by_id", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<UserDetailModel> getUserByID(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("user_id") String id) {

        ResponseEntity<UserDetailModel> responseEntity;
        UserDetailModel detailsModel = new UserDetailModel();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            List<GetPost> list = dbServiceImpl.getPosts(id);
           List<User> userDto = dbServiceImpl.getUserProfile(id);

            detailsModel.setUser_posts(list);
            detailsModel.setUser(userDto.get(0));
            detailsModel.setSuccess("true");
            detailsModel.setMessage("fetch successfully");
            responseEntity = new ResponseEntity<UserDetailModel>(detailsModel, HttpStatus.OK);// 200

        } else {
            detailsModel.setSuccess("false");
            detailsModel.setMessage("invalid request");
            responseEntity = new ResponseEntity<UserDetailModel>(detailsModel, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
    
     @RequestMapping(value = "/get_comunity_comment", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getComunityComment(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
     //   if (headerValid) {
            if (user_id != null) {
                List<GetComunityComments> list = dbServiceImpl.getComunityComments(user_id);
                if (list.size() > 0) {
                    baseResponse.setObject(list);
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment fetch successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//                } else {
//                    baseResponse.setObject(list);
//                    baseResponse.setSuccess("true");
//                    baseResponse.setMessage("Comment not found");
//                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 400
//                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameter !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }

        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
    
    
     @RequestMapping(value = "/update_user_by_id", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> updateProfile(@RequestHeader("authenticate") String authenticate,
     @RequestParam("user_id") String user_id, @RequestParam("user_name") String user_name, 
     @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("introduction") String introduction, @RequestParam("image") String image) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       
               int res = dbServiceImpl.update_profile(user_id, user_name, email,password, introduction, image);
                if (res != 0) {                 
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Profile Updated successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                              
                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
                }
            
        return responseEntity;
    }
    
     @RequestMapping(value = "/get_user_profile", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getUserProfile(@RequestHeader("authenticate") String authenticate,
     @RequestParam("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       
               List<User> res = dbServiceImpl.getUserProfile(user_id);
                if (res != null) {                 
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("successful");
                        baseResponse.setObject(res);
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                              
                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
                }
            
        return responseEntity;
    }
    
      @RequestMapping(value = "/add_category_comment", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> addCategoryComment(@RequestHeader("authenticate") String authenticate,
                @RequestHeader("user_id") String user_id, @RequestBody AddCategoryCommentModel addCategoryCommentModel) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
       // if (headerValid) {
            if (addCategoryCommentModel != null) {
                int res = dbServiceImpl.addCategoryCommentData(addCategoryCommentModel, user_id);
                if (res > 0) {
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//                } else {
//                    baseResponse.setSuccess("false");
//                    baseResponse.setMessage("Failed !");
//                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
//                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameters !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
    
      @RequestMapping(value = "/comunity_comment_like", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> comunityCommentLike(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("comment_id") String comment_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            if (comment_id.trim().length() > 0) {
                int res = dbServiceImpl.likeComynityComment(comment_id, user_id);
                if (res > 0) {
                    if (res == 1) {
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Like successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                    } else {
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("UnLike successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                    }

                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameters !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
    
     @RequestMapping(value = "/delete_post_comment", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> postCommentDelete(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestBody PostComment postComment) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
       // if (headerValid) {
            if (postComment != null) {
                int res = dbServiceImpl.postCommentDelete(postComment, user_id);
                if (res > 0) {
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment deleted successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//                } else {
//                    baseResponse.setSuccess("false");
//                    baseResponse.setMessage("Failed !");
//                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
//                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameters !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
    
       @RequestMapping(value = "/block_user", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> BlockUser(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("blocked_user_id") String blocked_user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        
                int res = dbServiceImpl.blockedUser(user_id,blocked_user_id);
                if (res > 0) {         
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Blocked successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200          

                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
                }  
                
        return responseEntity;
    }
    
    @RequestMapping(value = "/get_followers_list", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getFollowersList(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
     //   if (headerValid) {
            if (user_id != null) {
                List<GetFollowers> list = dbServiceImpl.getFollowersList(user_id);
                if (list.size() > 0) {
                    baseResponse.setObject(list);
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("followers fetch successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//                } else {
//                    baseResponse.setObject(list);
//                    baseResponse.setSuccess("true");
//                    baseResponse.setMessage("Comment not found");
//                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 400
//                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameter !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }

        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
   
    @RequestMapping(value = "/get_follow_list", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getFollowList(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
     //   if (headerValid) {
            if (user_id != null) {
                List<GetFollowers> list = dbServiceImpl.getFollowList(user_id);
                if (list.size() > 0) {
                    baseResponse.setObject(list);
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("follow fetch successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//                } else {
//                    baseResponse.setObject(list);
//                    baseResponse.setSuccess("true");
//                    baseResponse.setMessage("Comment not found");
//                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 400
//                }
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Null Parameter !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }

        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
     @RequestMapping(value = "/get_user_post", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getUserPost(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
       // if (headerValid) {
            List<GetPost> listPosts = dbServiceImpl.getUserPosts(user_id);
            if (listPosts.size() > 0) {
                baseResponse.setObject(listPosts);
                baseResponse.setSuccess("true");
                baseResponse.setMessage("Post fetch successfully");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//            } else {
//                baseResponse.setSuccess("false");
//                baseResponse.setMessage("Failed !");
//                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
//            }

        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }
    
    @RequestMapping(value = "/add_follow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> addFollow(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("follower_id") String follower_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       
            if (follower_id.trim().length() > 0) {
                int res = dbServiceImpl.addFollowUser(user_id, follower_id);                       
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Follow user successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200                
                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        
        return responseEntity;
    }
    
     @RequestMapping(value = "/delete_follow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> deleteFollow(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("follower_id") String follower_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       
            if (follower_id.trim().length() > 0) {
                int res = dbServiceImpl.deleteFollowUser(user_id, follower_id);                       
                        baseResponse.setSuccess("true");
                        baseResponse.setMessage("Unfollow user successfully");
                        responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200                
                } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Failed !");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }
        
        return responseEntity;
    }
    
    @RequestMapping(value = "/get_calender_bookings", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getCalenderBookings(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
       // boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
       // if (headerValid) {
            List<GetCalBooking> listPosts = dbServiceImpl.getCalenderBooking(user_id);
            if (listPosts.size() > 0) {
                baseResponse.setObject(listPosts);
                baseResponse.setSuccess("true");
                baseResponse.setMessage("Bookings fetch successfully");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
//            } else {
//                baseResponse.setSuccess("false");
//                baseResponse.setMessage("Failed !");
//                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
//            }

        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
        return responseEntity;
    }

    
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> forgotPassword(@RequestParam("email") String email) {
        BaseResponse baseResponse = new BaseResponse();
        if (email != null) {
            User user = dbServiceImpl.checkUser(email);
            if (user != null) {
               
                baseResponse.setObject(user);
                baseResponse.setSuccess("true");
                baseResponse.setMessage("Password sent on email Successfully");
                ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                        HttpStatus.OK);
                  return responseEntity;
               
              
            } else {
                    baseResponse.setSuccess("false");
                    baseResponse.setMessage("Invalid email or password!");
                    ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                            HttpStatus.BAD_REQUEST);
                    return responseEntity;
            }
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("Something went wrong");
            ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                    HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }
 
}
