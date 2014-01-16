package controllers

import play.api.mvc.{Action, Controller}
import models.Video
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.mvc.Flash

/**
 * Ethan Petuchowski
 * 1/11/14
 *
 * Handles HTTP requests (via 'conf/routes')
 * specific to the Video model
 * and generates responses for views
 */

object Videos extends Controller {
  private def videoForm: Form[Video] = Form(
    mapping(
      "url" -> nonEmptyText.verifying("A video with this ID already exists", Video.findByURL(_).isEmpty),
      "title" -> nonEmptyText
    )(Video.makeVideoFromURL)(Video.unapply)
  )

  def list = Action { implicit request =>
    val videos = Video.findAll
    Ok(views.html.videos.list(videos))
  }

  def show(id: String) = Action { implicit request =>
    Video.findByID(id).map { video =>
      Ok(views.html.videos.details(video))
    }.getOrElse(NotFound)
  }

  def newVideo = Action { implicit request =>
    val form = if (flash.get("error").isDefined)
      videoForm.bind(flash.data)
    else
      videoForm
    Ok(views.html.videos.editVideo(form))
  }

  def save = Action { implicit request =>
    val newVideoForm : Form[Video] = videoForm.bindFromRequest()

    newVideoForm.fold(
      hasErrors = { form =>
        Redirect(routes.Videos.newVideo()).
          flashing(Flash(form.data) +
            ("error" -> "Please correct the errors in the form."))
      },
      success = { newVideo =>
        Video.add(newVideo)
        val message = "Successfully added video " + newVideo.title
        Redirect(routes.Videos.show(newVideo.id)).
          flashing("success" -> message)
      }
    )
  }
}
