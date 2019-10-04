package com.wa.rumbo;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import models.GetComments;
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
            @RequestParam("device_token") String device_token) {
        BaseResponse baseResponse = new BaseResponse();
        if (device_id != null) {
            User user = dbServiceImpl.checkUser(device_id);
            if (user != null) {
                baseResponse.setObject(user);
                baseResponse.setSuccess("true");
                baseResponse.setMessage("Login Successfully");
                ResponseEntity<BaseResponse> responseEntity = new ResponseEntity<BaseResponse>(baseResponse,
                        HttpStatus.OK);
                return responseEntity;
            } else {
                int res = dbServiceImpl.insertUser(device_id, device_token);
                if (res != 0) {
                    User user2 = dbServiceImpl.checkUser(device_id);
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

    @RequestMapping(value = "/category_list", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> categoryList(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            List<Categories> categories = dbServiceImpl.getCategories();
            baseResponse.setObject(categories);
            baseResponse.setSuccess("true");
            baseResponse.setMessage("Data fetch sucessfully");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
        } else {
            baseResponse.setSuccess("false");
            baseResponse.setMessage("invalid request");
            responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);// 401
        }
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
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            List<GetPost> listPosts = dbServiceImpl.getPosts(user_id);
            if (listPosts.size() > 0) {
                baseResponse.setObject(listPosts);
                baseResponse.setSuccess("true");
                baseResponse.setMessage("Post fetch successfully");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
            } else {
                baseResponse.setSuccess("false");
                baseResponse.setMessage("Failed !");
                responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);// 400
            }

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
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            if (postComment != null) {
                int res = dbServiceImpl.postComment(postComment, user_id);
                if (res > 0) {
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment successfully");
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

    @RequestMapping(value = "/get_post_comment", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<BaseResponse> getPostComment(@RequestHeader("authenticate") String authenticate,
            @RequestHeader("user_id") String user_id, @RequestParam("post_id") String post_id) {
        ResponseEntity<BaseResponse> responseEntity;
        BaseResponse baseResponse = new BaseResponse();
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
            if (post_id != null) {
                List<GetComments> list = dbServiceImpl.getPostComments(post_id, user_id);
                if (list.size() > 0) {
                    baseResponse.setObject(list);
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment fetch successfully");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 200
                } else {
                    baseResponse.setObject(list);
                    baseResponse.setSuccess("true");
                    baseResponse.setMessage("Comment not found");
                    responseEntity = new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);// 400
                }
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
        boolean headerValid = dbServiceImpl.isHeaderValid(authenticate, user_id);
        if (headerValid) {
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

            } else {
                detailsModel.setSuccess("false");
                detailsModel.setMessage("Null Parameter !");
                responseEntity = new ResponseEntity<PostDetailModel>(detailsModel, HttpStatus.BAD_REQUEST);// 400
            }
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

                        String message = dbServiceImpl.insertNotificationData(comment_id, user_id, post_id);
                        String device_token = dbServiceImpl.getDeviceTokenByPostID(post_id);

                        JSONObject details = new JSONObject();
                        details.put("comment_id", comment_id);
                        details.put("post_id", post_id);
                        sendAndroidNotification(details, device_token, message);

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
            User userDto = dbServiceImpl.getUserProfile(id);

            detailsModel.setUser_posts(list);
            detailsModel.setUser(userDto);
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
}
