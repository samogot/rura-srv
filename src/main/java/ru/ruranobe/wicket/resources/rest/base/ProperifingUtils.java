package ru.ruranobe.wicket.resources.rest.base;

import org.apache.wicket.validation.validator.RangeValidator;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.wicket.validators.AllowedFieldsValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ProperifingUtils
{
    public static void properifyVolume(Volume volume, HashSet<String> fields, HashSet<String> imageFields, VolumesMapper volumesMapper, ExternalResourcesMapper externalResourcesMapper)
    {
        if (!imageFields.isEmpty())
        {
            List<ExternalResource> covers = new ArrayList<>();
            ExternalResource volumeCover;
            volumeCover = externalResourcesMapper.getExternalResourceById(volume.getImageOne());
            if (volumeCover != null)
            {
                covers.add(volumeCover);
            }
            volumeCover = externalResourcesMapper.getExternalResourceById(volume.getImageTwo());
            if (volumeCover != null)
            {
                covers.add(volumeCover);
            }
            volumeCover = externalResourcesMapper.getExternalResourceById(volume.getImageThree());
            if (volumeCover != null)
            {
                covers.add(volumeCover);
            }
            volumeCover = externalResourcesMapper.getExternalResourceById(volume.getImageFour());
            if (volumeCover != null)
            {
                covers.add(volumeCover);
            }
            for (ExternalResource cover : covers)
            {
                FieldFilteringUtils.filterAllowedFields(cover, imageFields);
            }
            volume.setCovers(covers);
        }
        if (fields.contains("lastUpdateDate"))
        {
            volume.setLastUpdateDate(volumesMapper.getProjectUpdateDate(volume.getVolumeId()));
        }
        if (fields.contains("lastEditDate"))
        {
            volume.setLastEditDate(volumesMapper.getProjectEditDate(volume.getVolumeId()));
        }
        FieldFilteringUtils.filterAllowedFields(volume, fields);
    }

    public static void properifyProject(Project project, HashSet<String> fields, HashSet<String> imageFields, ProjectsMapper projectsMapper, ExternalResourcesMapper externalResourcesMapper)
    {
        if (!imageFields.isEmpty() && project.getImageId() != null)
        {
            ExternalResource image = externalResourcesMapper.getExternalResourceById(project.getImageId());
            FieldFilteringUtils.filterAllowedFields(image, imageFields);
            project.setImage(image);
        }
        if (fields.contains("lastUpdateDate"))
        {
            project.setLastUpdateDate(projectsMapper.getProjectUpdateDate(project.getProjectId()));
        }
        if (fields.contains("lastEditDate"))
        {
            project.setLastEditDate(projectsMapper.getProjectEditDate(project.getProjectId()));
        }
        FieldFilteringUtils.filterAllowedFields(project, fields);
    }
    public static final RangeValidator RESULT_LIMIT_VALIDATOR = RangeValidator.range(1, 100);
    public static final RangeValidator RESULT_PAGE_VALIDATOR = RangeValidator.minimum(1);
    private static final List<String> ALLOWED_PROJECT_FIELD_LIST = Arrays.asList("projectId",
            "imageWidth", "imageHeight", "imageMimeType", "imageUrl", "imageThumbnail", "imageTitle", "imageUploadedWhen",
            "url", "title", "nameJp", "nameEn", "nameRu", "nameRomaji", "author", "illustrator", "originalDesign",
            "originalStory", "orderNumber", "bannerHidden", "projectHidden", "onevolume", "works", "franchise",
            "annotation", "forumId", "status", "issueStatus", "translationStatus", "lastUpdateDate", "lastEditDate");
    public static final AllowedFieldsValidator ALLOWED_PROJECT_FIELD_VALIDATOR =
            new AllowedFieldsValidator(ALLOWED_PROJECT_FIELD_LIST).setParamName("fields");
    private static final List<String> ALLOWED_SUBPROJECT_FIELD_LIST = Arrays.asList("projectId", "parentId", "title", "forumId");
    public static final AllowedFieldsValidator ALLOWED_SUBPROJECT_FIELD_VALIDATOR =
            new AllowedFieldsValidator(ALLOWED_SUBPROJECT_FIELD_LIST).setParamName("fields");
    private static final List<String> ALLOWED_VOLUMES_FIELD_LIST = Arrays.asList("volumeId", "projectId", "imageWidth",
            "imageHeight", "imageMimeType", "imageUrl", "imageThumbnail", "imageTitle", "imageUploadedWhen", "url",
            "nameFile", "nameTitle", "nameJp", "nameEn", "nameRu", "nameRomaji", "nameShort", "sequenceNumber", "author",
            "illustrator", "originalDesign", "originalStory", "releaseDate", "isbn", "externalUrl", "volumeType",
            "volumeStatus", "volumeStatusHint", "adult", "annotation", "lastUpdateDate", "lastEditDate");
    public static final AllowedFieldsValidator ALLOWED_VOLUMES_FIELD_VALIDATOR =
            new AllowedFieldsValidator(ALLOWED_VOLUMES_FIELD_LIST).setParamName("fields");
    private static final List<String> ALLOWED_UPDATES_FIELD_LIST = Arrays.asList("updateId", "projectId", "volumeId",
            "chapterId", "updateType", "showTime", "description");
    public static final AllowedFieldsValidator ALLOWED_UPDATES_FIELD_VALIDATOR =
            new AllowedFieldsValidator(ALLOWED_UPDATES_FIELD_LIST).setParamName("fields");
    private static final List<String> ALLOWED_MEMBERS_FIELD_LIST = Arrays.asList("memberId", "userId", "teamId", "nickname");
    public static final AllowedFieldsValidator ALLOWED_MEMBERS_FIELD_VALIDATOR =
            new AllowedFieldsValidator(ALLOWED_MEMBERS_FIELD_LIST).setParamName("fields");
}
