package com.amazonaws.ecr.repository;

import com.amazonaws.cloudformation.exceptions.TerminalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.DeleteLifecyclePolicyRequest;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryPolicyRequest;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.DescribeRepositoriesRequest;
import software.amazon.awssdk.services.ecr.model.GetLifecyclePolicyRequest;
import software.amazon.awssdk.services.ecr.model.GetRepositoryPolicyRequest;
import software.amazon.awssdk.services.ecr.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.ecr.model.PutLifecyclePolicyRequest;
import software.amazon.awssdk.services.ecr.model.SetRepositoryPolicyRequest;
import software.amazon.awssdk.services.ecr.model.Tag;
import software.amazon.awssdk.services.ecr.model.TagResourceRequest;
import software.amazon.awssdk.services.ecr.model.UntagResourceRequest;

public class Translator {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    static CreateRepositoryRequest createRepositoryRequest(final ResourceModel model) {
        return CreateRepositoryRequest.builder()
                .repositoryName(model.getRepositoryName())
                .tags(translateTagsToSdk(model.getTags()))
                .build();
    }

    static PutLifecyclePolicyRequest putLifecyclePolicyRequest(final ResourceModel model) {
        return PutLifecyclePolicyRequest.builder()
                .repositoryName(model.getRepositoryName())
                .lifecyclePolicyText(model.getLifecyclePolicy().getLifecyclePolicyText())
                .registryId(model.getLifecyclePolicy().getRegistryId())
                .build();
    }

    static DeleteLifecyclePolicyRequest deleteLifecyclePolicyRequest(final ResourceModel model) {
        return DeleteLifecyclePolicyRequest.builder()
                .repositoryName(model.getRepositoryName())
                .build();
    }

    static SetRepositoryPolicyRequest setRepositoryPolicyRequest(final ResourceModel model) {
        try {
            return SetRepositoryPolicyRequest.builder()
                    .repositoryName(model.getRepositoryName())
                    .policyText(MAPPER.writeValueAsString(model.getRepositoryPolicyText()))
                    .build();
        } catch (final JsonProcessingException e) {
            throw new TerminalException(e);
        }
    }

    static DeleteRepositoryPolicyRequest deleteRepositoryPolicyRequest(final ResourceModel model) {
        return DeleteRepositoryPolicyRequest.builder()
                .repositoryName(model.getRepositoryName())
                .build();
    }

    static DeleteRepositoryRequest deleteRepositoryRequest(final ResourceModel model) {
        return DeleteRepositoryRequest.builder()
                .repositoryName(model.getRepositoryName())
                .build();
    }

    static TagResourceRequest tagResourceRequest(final List<Tag> tags, final String arn) {
        return TagResourceRequest.builder().tags(tags).resourceArn(arn).build();
    }

    static UntagResourceRequest untagResourceRequest(final Collection<String> tagKeys, final String arn) {
        return UntagResourceRequest.builder().tagKeys(tagKeys).resourceArn(arn).build();
    }

    static ListTagsForResourceRequest listTagsForResourceRequest(final String arn) {
        return ListTagsForResourceRequest.builder().resourceArn(arn).build();
    }

    static DescribeRepositoriesRequest describeRepositoriesRequest(final ResourceModel model) {
        return DescribeRepositoriesRequest.builder()
                .repositoryNames(Arrays.asList(model.getRepositoryName()))
                .build();
    }

    static DescribeRepositoriesRequest describeRepositoriesRequest(final String nextToken) {
        return DescribeRepositoriesRequest.builder()
                .maxResults(50)
                .nextToken(nextToken)
                .build();
    }

    static List<Tag> translateTagsToSdk(final Set<com.amazonaws.ecr.repository.Tag> tags) {
        if (tags == null) return null;
        return tags.stream().map(tag -> Tag.builder()
                .key(tag.getKey())
                .value(tag.getValue())
                .build()
        ).collect(Collectors.toList());
    }

    static Set<com.amazonaws.ecr.repository.Tag> translateTagsFromSdk(final List<Tag> tags) {
        if (tags == null) return null;
        return tags.stream().map(tag -> com.amazonaws.ecr.repository.Tag.builder()
                .key(tag.key())
                .value(tag.value())
                .build()
        ).collect(Collectors.toSet());
    }

    static GetRepositoryPolicyRequest getRepositoryPolicyRequest(final String repositoryName, final String registryId) {
        return GetRepositoryPolicyRequest.builder()
                .repositoryName(repositoryName)
                .registryId(registryId)
                .build();
    }

    static GetLifecyclePolicyRequest getLifecyclePolicyRequest(final String repositoryName, final String registryId) {
        return GetLifecyclePolicyRequest.builder()
                .repositoryName(repositoryName)
                .registryId(registryId)
                .build();
    }
}